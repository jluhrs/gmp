package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.giapi.status.AlarmState;
import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.epicstostatus.generated.AlarmChannelType;
import edu.gemini.aspen.gmp.epicstostatus.generated.Channels;
import edu.gemini.aspen.gmp.epicstostatus.generated.HealthChannelType;
import edu.gemini.aspen.gmp.epicstostatus.generated.SimpleChannelType;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.NewEpicsReader;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.api.ReadOnlyChannel;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.shared.util.immutable.Pair;
import edu.gemini.shared.util.immutable.Tuple2;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsToStatusComponent
 *
 * @author Nicolas A. Barriga
 *         Date: 1/24/12
 */
@Component
public class EpicsToStatusComponent {
    private static final Logger LOG = Logger.getLogger(EpicsToStatusComponent.class.getName());
    /**
     * Structure mapping Giapi Status Item -> Epics PV
     */
    private final Map<String, Tuple2<StatusSetter, ReadOnlyClientEpicsChannel<?>>> channelMap = new HashMap<String, Tuple2<StatusSetter, ReadOnlyClientEpicsChannel<?>>>();
    private final Map<String, ReadOnlyChannel<?>> alarmChannelMap = new HashMap<String, ReadOnlyChannel<?>>();
    private final Map<String, ReadOnlyChannel<String>> healthChannelMap = new HashMap<String, ReadOnlyChannel<String>>();

    private final NewEpicsReader _reader;
    private final String xmlFileName;
    private final String xsdFileName;
    private final JmsProvider provider;

    private static final String NAME = "GMP_EPICS_TO_STATUS";

    public EpicsToStatusComponent(@Requires NewEpicsReader reader,
                                  @Requires JmsProvider provider,
                                  @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName,
                                  @Property(name = "xsdFileName", value = "INVALID", mandatory = true) String xsdFileName) {
        _reader = reader;
        this.xmlFileName = xmlFileName;
        this.xsdFileName = xsdFileName;
        this.provider = provider;
    }

    @Validate
    public void initialize() {
        EpicsToStatusConfiguration conf = new EpicsToStatusConfiguration(xmlFileName, xsdFileName);
        initialize(conf.getSimulatedChannels());
    }

    /**
     * Initialize the EpicsStatusService. Creates appropriate channels in an ChannelAccessServer.
     *
     * @param items channels to create and listen to.
     */
    public void initialize(Channels items) {
        for (final SimpleChannelType item : items.getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            try {
                ReadOnlyClientEpicsChannel ch = _reader.getChannelAsync(item.getEpicschannel());
                StatusSetter ss = new StatusSetter(NAME + item.getStatusitem(), item.getStatusitem());
                try {
                    ss.startJms(provider);
                } catch (JMSException e) {
                    LOG.log(Level.SEVERE, "Won't be able to publish status updates for channel: " + item.getEpicschannel() + ", " + e.getMessage(), e);
                    ch.destroy();
                    continue;
                }
                channelMap.put(item.getEpicschannel(), new Pair<StatusSetter, ReadOnlyClientEpicsChannel<?>>(ss, ch));
                try {
                    ChannelListener<?> chL;
                    if (item instanceof HealthChannelType) {
                        chL = new ChannelListener<String>() {
                            @Override
                            public void valueChanged(String channelName, List<String> values) {
                                try {
                                    channelMap.get(channelName)._1().setStatusItem(new HealthStatus(item.getStatusitem(), Health.valueOf(values.get(item.getIndex() != null ? item.getIndex() : 0))));
                                } catch (JMSException e) {
                                    LOG.log(Level.SEVERE, e.getMessage(), e);
                                } catch (IllegalArgumentException e) {
                                    LOG.log(Level.SEVERE, e.getMessage(), e);
                                }
                            }
                        };
                        //TODO: implement proper alarm handling, currently an AlarmStatusItem is created, but the alarm is always OFF
                    } else if (item instanceof AlarmChannelType) {
                        chL = new ChannelListener() {
                            @Override
                            public void valueChanged(String channelName, List values) {
                                try {
                                    channelMap.get(channelName)._1().setStatusItem(new AlarmStatus(item.getStatusitem(), values.get(item.getIndex() != null ? item.getIndex() : 0), AlarmState.DEFAULT));
                                } catch (JMSException e) {
                                    LOG.log(Level.SEVERE, e.getMessage(), e);
                                }
                            }
                        };
                    } else/* (item instanceof SimpleChannelType)*/ {
                        chL = new ChannelListener() {
                            @Override
                            public void valueChanged(String channelName, List values) {
                                try {
                                    channelMap.get(channelName)._1().setStatusItem(new BasicStatus(item.getStatusitem(), values.get(item.getIndex() != null ? item.getIndex() : 0)));
                                } catch (JMSException e) {
                                    LOG.log(Level.SEVERE, e.getMessage(), e);
                                }
                            }
                        };
                    }
                    ch.registerListener(chL);
                    LOG.info("Successfully created status item publisher for EPICS channel: " + item.getEpicschannel());
                } catch (IllegalStateException ex) {
                    LOG.log(Level.SEVERE, "Couldn't register listener for channel: " + item.getEpicschannel() + ", " + ex.getMessage(), ex);
                    ch.destroy();
                }
            } catch (CAException ex) {
                LOG.log(Level.SEVERE, "Couldn't connect to channel: " + item.getEpicschannel() + ", " + ex.getMessage(), ex);
            } catch (EpicsException ex) {
                LOG.log(Level.SEVERE, "Couldn't connect to channel: " + item.getEpicschannel() + ", " + ex.getMessage(), ex);
            }
        }
    }


    @Invalidate
    public void shutdown() {
        for (Tuple2<StatusSetter, ReadOnlyClientEpicsChannel<?>> pair : channelMap.values()) {
            pair._1().stopJms();
            try {
                pair._2().destroy();
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        channelMap.clear();
    }
}
