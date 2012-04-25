package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.shared.util.immutable.Option;
import org.apache.felix.ipojo.annotations.*;

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
public class LocalStatusItemTranslatorImpl extends AbstractStatusItemTranslator implements StatusItemTranslator {
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
        super.start();
    }

    @Invalidate
    public void stop() {
        super.stop();
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        Option<StatusItem<?>> itemOpt = translate(item);

        //publish translation
        if (!itemOpt.isEmpty()) {
            aggregate.update(itemOpt.getValue());
        }
    }
}
