package edu.gemini.aspen.giapi.statusservice;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.gmp.top.Top;
import org.apache.felix.ipojo.annotations.*;
import org.xml.sax.SAXException;

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
public class InMemoryStatusItemTranslator extends AbstractStatusItemTranslator implements StatusItemTranslator {
    private static final Logger LOG = Logger.getLogger(InMemoryStatusItemTranslator.class.getName());
    private final StatusHandlerAggregate aggregate;
    private final StatusDatabaseService statusDatabase;

    public InMemoryStatusItemTranslator(@Requires Top top,
                                        @Requires StatusHandlerAggregate aggregate,
                                        @Requires StatusDatabaseService statusDatabase,
                                        @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName) {
        super(top, xmlFileName);
        Preconditions.checkArgument(aggregate != null);
        Preconditions.checkArgument(statusDatabase != null);
        this.aggregate = aggregate;
        this.statusDatabase = statusDatabase;
    }

    @Validate
    public void start() throws IOException, JAXBException, SAXException {
        LOG.info("Start validate");
        super.start();
        initItems();
        LOG.info("End validate");
    }

    @Override
    protected void initItems() {
        for (StatusItem<?> item : statusDatabase.getAll()) {
            update(item);
        }
    }

    @Invalidate
    public void stop() {
        LOG.info("Start stop");
        super.stop();
        LOG.info("End stop");
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        for (StatusItem<?> newItem : translate(item)) {
            LOG.finer("Publishing translated status item: " + newItem);
            aggregate.update(newItem);
        }
    }
}
