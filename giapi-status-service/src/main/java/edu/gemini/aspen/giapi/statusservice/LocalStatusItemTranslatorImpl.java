package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.shared.util.immutable.Option;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Class LocalStatusItemTranslatorImpl will publish status items translations directly back to the status handler aggregate
 *
 * @author Nicolas A. Barriga
 *         Date: 4/5/12
 */
@Component
@Provides
public class LocalStatusItemTranslatorImpl extends AbstractStatusItemTranslator implements JmsArtifact, StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(LocalStatusItemTranslatorImpl.class.getName());
    private final StatusHandlerAggregate aggregate;

    public LocalStatusItemTranslatorImpl(@Requires Top top,
                                         @Requires StatusHandlerAggregate aggregate,
                                         @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName) {
        super(top, xmlFileName);
        this.aggregate = aggregate;
    }

    @Validate
    public void start() throws IOException, JAXBException {
        LOG.finer("Start validate");
        super.start();
        validated=true;
        if(validated&&jmsStarted){
            initItems();
        }
        LOG.finer("End validate");
    }

    @Invalidate
    public void stop() {
        LOG.finer("Start stop");
        validated=false;
        super.stop();
        LOG.finer("End stop");
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        LOG.fine("Status item received: "+item);
        Option<StatusItem<?>> itemOpt = translate(item);

        //publish translation
        if (!itemOpt.isEmpty()) {
            LOG.fine("Publishing translated status item: "+itemOpt);
            aggregate.update(itemOpt.getValue());
        }
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.finer("Start startJms");
        getter.startJms(provider);
        jmsStarted=true;
        if(validated&&jmsStarted){
            initItems();
        }
        LOG.finer("End startJms");
    }

    @Override
    public void stopJms() {
        LOG.finer("Start stopJms");
        jmsStarted=false;
        getter.stopJms();
        LOG.finer("End stopJms");
    }
}
