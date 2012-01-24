package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.giapi.status.AlarmState;
import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.aspen.gmp.epicstostatus.generated.*;
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
    private final EpicsTop epicsTop;

    private static final String NAME = "GMP_EPICS_TO_STATUS";

    public EpicsToStatusComponent(@Requires NewEpicsReader reader,
                                  @Requires EpicsTop epicsTop,
                                  @Requires JmsProvider provider,
                                  @Property(name = "xmlFileName", value = "INVALID", mandatory = true) String xmlFileName,
                                  @Property(name = "xsdFileName", value = "INVALID", mandatory = true) String xsdFileName) {
        _reader = reader;
        this.xmlFileName = xmlFileName;
        this.xsdFileName = xsdFileName;
        this.epicsTop = epicsTop;
        this.provider=provider;
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
        for (final BaseChannelType item : items.getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            try {
                if (item instanceof HealthChannelType) {
                    ReadOnlyClientEpicsChannel<String> ch = _reader.getStringChannel(item.getEpicschannel());
                    StatusSetter ss=new StatusSetter(NAME, item.getStatusitem());
                    try {
                        ss.startJms(provider);
                    } catch (JMSException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                        continue;
                    }
                    channelMap.put(item.getEpicschannel(), new Pair<StatusSetter, ReadOnlyClientEpicsChannel<?>>(ss, ch));
                    ch.registerListener(new ChannelListener<String>() {
                        @Override
                        public void valueChanged(String channelName, List<String> values) {
                            try {
                                channelMap.get(channelName)._1().setStatusItem(new HealthStatus(item.getStatusitem(), Health.valueOf(values.get(0))));
                            } catch (JMSException e) {
                                LOG.log(Level.SEVERE, e.getMessage(), e);
                            } catch (IllegalArgumentException e) {
                                LOG.log(Level.SEVERE, e.getMessage(), e);
                            }
                        }
                    });
                } else if (item instanceof AlarmChannelType) {
                    ReadOnlyClientEpicsChannel ch = _reader.getChannelAsync(item.getEpicschannel());
                    StatusSetter ss=new StatusSetter(NAME, item.getStatusitem());
                    try {
                        ss.startJms(provider);
                    } catch (JMSException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                        continue;
                    }
                    channelMap.put(item.getEpicschannel(), new Pair<StatusSetter, ReadOnlyClientEpicsChannel<?>>(ss, ch));
                    ch.registerListener(new ChannelListener() {
                        @Override
                        public void valueChanged(String channelName, List values) {
                            try {
                                channelMap.get(channelName)._1().setStatusItem(new AlarmStatus(item.getStatusitem(), values.get(0), AlarmState.DEFAULT));
                            } catch (JMSException e) {
                                LOG.log(Level.SEVERE, e.getMessage(), e);
                            }
                        }
                    });
                } else if (item instanceof SimpleChannelType) {
                    ReadOnlyClientEpicsChannel ch = _reader.getChannelAsync(item.getEpicschannel());
                    StatusSetter ss=new StatusSetter(NAME, item.getStatusitem());
                    try {
                        ss.startJms(provider);
                    } catch (JMSException e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                        continue;
                    }
                    channelMap.put(item.getEpicschannel(), new Pair<StatusSetter, ReadOnlyClientEpicsChannel<?>>(ss, ch));
                    ch.registerListener(new ChannelListener() {
                        @Override
                        public void valueChanged(String channelName, List values) {
                            try {
                                channelMap.get(channelName)._1().setStatusItem(new BasicStatus(item.getStatusitem(), values.get(0)));
                            } catch (JMSException e) {
                                LOG.log(Level.SEVERE, e.getMessage(), e);
                            }
                        }
                    });
                }
            } catch (CAException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
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
