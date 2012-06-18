package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.statusservice.generated.DataType;
import edu.gemini.aspen.giapi.statusservice.generated.MapType;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.shared.util.immutable.ImOption;
import edu.gemini.shared.util.immutable.None;
import edu.gemini.shared.util.immutable.Option;
import net.jmatrix.eproperties.EProperties;

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
 * Base class for building status translators
 */
abstract public class AbstractStatusItemTranslator implements StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(AbstractStatusItemTranslator.class.getName());
    private static final String CONF_DIR_PROPERTY = "statusTranslatorFile";
    private final Map<String, String> names = new HashMap<String, String>();
    private final Map<String, DataType> types = new HashMap<String, DataType>();
    private final Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();
    private final Map<String, String> defaults = new HashMap<String, String>();
    private final String xmlFileName;
    private final String name = "StatusItemTranslator: " + this;
    protected final Top top;
    protected StatusItemTranslatorConfiguration config;
    protected final StatusGetter getter = new StatusGetter("Status Translator initial item loader");
    protected boolean jmsStarted = false, validated = false;

    public AbstractStatusItemTranslator(Top top, String xmlFileName) {
        this.top = top;
        this.xmlFileName = xmlFileName;
    }

    /**
     * Reads in the configuration and stores it for using it in  translations
     *
     * @throws IOException
     * @throws JAXBException
     */
    public void start() throws IOException, JAXBException {
        File f = new File(substituteProperties(xmlFileName));
        if (!f.exists()) {
            LOG.severe("Configuration file " + f + " does not exist");
            return;
        }
        LOG.info("Start StatusItemTranslator with configuration file " + f);

        //read mappings
        config = new StatusItemTranslatorConfiguration(new FileInputStream(f));

        // initialize mappings
        for (StatusType status : config.getStatuses()) {
            Map<String, String> tr = new HashMap<String, String>();
            for (MapType map : status.getMaps().getMap()) {
                tr.put(map.getFrom(), map.getTo());
            }
            translations.put(top.buildStatusItemName(status.getOriginalName()), tr);
            if (status.getDefault() != null) {
                defaults.put(top.buildStatusItemName(status.getOriginalName()), status.getDefault());
            }
        }

        //store types and names
        for (StatusType status : config.getStatuses()) {
            types.put(top.buildStatusItemName(status.getOriginalName()), status.getTranslatedType());
            names.put(top.buildStatusItemName(status.getOriginalName()), top.buildStatusItemName(status.getTranslatedName()));
        }
    }

    /**
     * Try to fetch items from the StatusDB at startup. Translate those found.
     */
    protected void initItems() {
        LOG.finer("Start initItems");

        try {
            for (StatusItem<?> item : getter.getAllStatusItems()) {
                update(item);
            }
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        LOG.finer("End initItems");
    }

    private String substituteProperties(String url) {
        EProperties props = new EProperties();
        props.addAll(System.getProperties());
        props.put(CONF_DIR_PROPERTY, url);
        return props.get(CONF_DIR_PROPERTY, "/").toString();
    }

    public void stop() {

    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Create a StatusItem
     *
     * @param type    DataType of the item to create
     * @param newName name of the item
     * @param newVal  value of the item
     * @return a Some<StatusItem<?>> if everything is correct, otherwise a None.
     */
    private Option/*<StatusItem<?>>*/ createStatus(DataType type, String newName, String newVal) {
        StatusItem<?> newItem = null;
        try {
            switch (type) {
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
        } catch (NullPointerException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

        return ImOption.apply(newItem);
    }

    /**
     * Translate a StatusItem according to translations specified in the config file.
     *
     * @param item the item to translate
     * @param <T>
     * @return a Some<StatusItem<?>> with the translated item, or a None if a problem occured
     */
    protected <T> Option<StatusItem<?>> translate(StatusItem<T> item) {
        LOG.fine("Translating " + item);
        String newName = names.get(item.getName());

        //if there is no translation for this item, return None
        if (translations.get(item.getName()) == null) {
            return None.instance();
        }

        //translate
        String newVal = translations.get(item.getName()).get(item.getValue().toString());

        //no translation, return default
        if (newVal == null) {
            if (defaults.get(item.getName()) != null) {
                return createStatus(types.get(item.getName()), newName, defaults.get(item.getName()));
            } else {
                //no default, ignore
                return None.instance();
            }
        }

        //return proper translation
        return createStatus(types.get(item.getName()), newName, newVal);
    }
}
