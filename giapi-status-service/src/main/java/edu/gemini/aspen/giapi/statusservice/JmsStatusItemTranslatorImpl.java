package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.statusservice.generated.StatusType;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.shared.util.immutable.Option;
import org.apache.felix.ipojo.annotations.*;
import org.xml.sax.SAXException;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class JmsStatusItemTranslatorImpl will publish status items translations over JMS
 *
 * @author Nicolas A. Barriga
 *         Date: 4/5/12
 */
@Component
@Provides
public class JmsStatusItemTranslatorImpl extends AbstractStatusItemTranslator implements JmsArtifact, StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(JmsStatusItemTranslatorImpl.class.getName());
    private final Map<String, StatusSetter> setters = new HashMap<String, StatusSetter>();
    private JmsProvider provider;

    public JmsStatusItemTranslatorImpl(@Requires Top top,
                                       @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName) {
        super(top, xmlFileName);
    }

    @Validate
    public void start() throws IOException, JAXBException, SAXException {
        super.start();
        //create status setters
        for (StatusType status : config.getStatuses()) {
            setters.put(
                    top.buildStatusItemName(status.getOriginalName()),
                    new StatusSetter(
                            this.getName() + status.getOriginalName(),
                            top.buildStatusItemName(status.getOriginalName())));
        }
        validated=true;
        if(validated&&jmsStarted){
            initSetters();
            initItems();
        }
    }

    /**
     * Connect JMS on the StatusSetters
     */
    private void initSetters(){
        for (StatusSetter ss : setters.values()) {
            try {
                ss.startJms(provider);
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Invalidate
    public void stop() {
        validated=false;
        super.stop();
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        getter.startJms(provider);
        this.provider = provider;
        jmsStarted=true;
        if(validated&&jmsStarted){
            initSetters();
            initItems();
        }
    }

    @Override
    public void stopJms() {
        jmsStarted=false;
        getter.stopJms();
        for (StatusSetter ss : setters.values()) {
            ss.stopJms();
        }
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        Option<StatusItem<?>> itemOpt = translate(item);

        //publish translation
        if (!itemOpt.isEmpty()) {
            try {
                setters.get(item.getName()).setStatusItem(itemOpt.getValue());
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
