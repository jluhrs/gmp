package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.statusservice.generated.DataType;
import edu.gemini.aspen.giapi.statusservice.generated.MapType;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import net.jmatrix.eproperties.EProperties;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class StatusItemTranslatorImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 4/5/12
 */
@Component
@Provides
public class StatusItemTranslatorImpl implements JmsArtifact, StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(StatusItemTranslatorImpl.class.getName());
    private static final String CONF_DIR_PROPERTY = "statusTranslatorFile";
    private final Map<String, StatusSetter> setters = new HashMap<String, StatusSetter>();
    private final Map<String, String> names = new HashMap<String, String>();
    private final Map<String, DataType> types = new HashMap<String, DataType>();
    private final Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();
    private final String xmlFileName;
    private final String name = "StatusItemTranslator: " + this;
    private final Top top;

    public StatusItemTranslatorImpl(@Requires Top top,
            @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName) {
        this.top = top;
        this.xmlFileName = xmlFileName;
    }

    @Validate
    public void start() throws IOException, JAXBException {
        File f = new File(substituteProperties(xmlFileName));
        if (!f.exists()) {
            LOG.severe("Configuration file " + f + " does not exist");
            return;
        }
        LOG.info("Start StatusItemTranslator with configuration file " + f);

        //read mappings
        StatusItemTranslatorConfiguration config = new StatusItemTranslatorConfiguration(new FileInputStream(f));

        // initialize mappings
        for (StatusType status : config.getStatuses()) {
            Map<String, String> tr = new HashMap<String, String>();
            for (MapType map : status.getMaps().getMap()) {
                tr.put(map.getFrom(), map.getTo());
            }
            translations.put(top.buildStatusItemName(status.getOriginalName()), tr);
        }
        //create status setters
        for (StatusType status : config.getStatuses()) {
            setters.put(
                    top.buildStatusItemName(status.getOriginalName()),
                    new StatusSetter(
                            this.getName() + status.getOriginalName(),
                            top.buildStatusItemName(status.getOriginalName())));
        }
        //store types and names
        for (StatusType status : config.getStatuses()) {
            types.put(top.buildStatusItemName(status.getOriginalName()), status.getTranslatedType());
            names.put(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName()));
        }
    }

    private String substituteProperties(String url) {
        EProperties props = new EProperties();
        props.addAll(System.getProperties());
        props.put(CONF_DIR_PROPERTY, url);
        return props.get(CONF_DIR_PROPERTY, "/").toString();
    }

    @Invalidate
    public void stop() {

    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        for (StatusSetter ss : setters.values()) {
            ss.startJms(provider);
        }
    }

    @Override
    public void stopJms() {
        for (StatusSetter ss : setters.values()) {
            ss.stopJms();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        //if there is no translation for this item, do nothing
        if (translations.get(item.getName()) == null) {
            return;
        }

        //translate
        String newVal = translations.get(item.getName()).get(item.getValue().toString());
        String newName = names.get(item.getName());
        StatusItem<?> newItem = null;
        try {
            switch (types.get(item.getName())) {
                case INT:
                    newItem = new BasicStatus<Integer>(newName, Integer.valueOf(newVal));
                    break;
                case FLOAT:
                    newItem = new BasicStatus<Float>(newName, Float.valueOf(newVal));
                    break;
                case DOUBLE:
                    newItem = new BasicStatus<Double>(newName, Double.valueOf(newVal));
                    break;
                case STRING:
                    newItem = new BasicStatus<String>(newName, newVal);
                    break;
                case HEALTH:
                    newItem = new HealthStatus(newName, Health.valueOf(newVal));
                    break;
            }
        } catch (NumberFormatException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }


        //publish translation
        if (newItem != null) {
            try {
                setters.get(item.getName()).setStatusItem(newItem);
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
