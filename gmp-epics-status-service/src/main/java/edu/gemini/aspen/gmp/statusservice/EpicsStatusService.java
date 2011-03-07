package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.status.*;
import edu.gemini.aspen.gmp.statusservice.generated.*;
import edu.gemini.cas.IAlarmChannel;
import edu.gemini.cas.IChannel;
import edu.gemini.cas.IChannelAccessServer;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import org.apache.felix.ipojo.annotations.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsStatusService, uses an IChannelAccessServer to create channels, listens
 * to the corresponding StatusItem updates and informs the server.
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
//TODO:support multiple values (array)
@Component
@Provides
public class EpicsStatusService implements StatusHandler {
    private static final Logger LOG = Logger.getLogger(EpicsStatusService.class.getName());
    private static final Map<AlarmCause, Status> CAUSE_MAP = new EnumMap<AlarmCause, Status>(AlarmCause.class);
    private static final Map<AlarmSeverity, Severity> SEVERITY_MAP = new EnumMap<AlarmSeverity, Severity>(AlarmSeverity.class);

    static {
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_OK, Status.NO_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_HIHI, Status.HIHI_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_HI, Status.HIGH_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_LO, Status.LOW_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_LOLO, Status.LOLO_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_OTHER, Status.SOFT_ALARM);//TODO: fix this mapping

        SEVERITY_MAP.put(AlarmSeverity.ALARM_OK, Severity.NO_ALARM);
        SEVERITY_MAP.put(AlarmSeverity.ALARM_WARNING, Severity.MINOR_ALARM);
        SEVERITY_MAP.put(AlarmSeverity.ALARM_FAILURE, Severity.MAJOR_ALARM);
    }

    private static final String NAME = "GMP_EPICS_STATUS_SERVICE";
    /**
     * Structure mapping Giapi Status Item -> Epics PV
     */
    private final Map<String, IChannel<?>> channelMap = new HashMap<String, IChannel<?>>();
    private final Map<String, IAlarmChannel<?>> alarmChannelMap = new HashMap<String, IAlarmChannel<?>>();
    private final Map<String, IChannel<String>> healthChannelMap = new HashMap<String, IChannel<String>>();

    @Requires
    private IChannelAccessServer _channelAccessServer;

    @Property(name = "xmlFileName", value = "INVALID", mandatory = true)
    private String xmlFileName;
    @Property(name = "xsdFileName", value = "INVALID", mandatory = true)
    private String xsdFileName;

    private EpicsStatusService() {

    }

    public EpicsStatusService(IChannelAccessServer ICas) {
        _channelAccessServer = ICas;
    }


    @Validate
    public void initialize() {
        EpicsStatusServiceConfiguration conf = new EpicsStatusServiceConfiguration(xmlFileName, xsdFileName);
        initialize(conf.getSimulatedChannels());
    }

    /**
     * Initialize the EpicsStatusService. Creates appropriate channels in an IChannelAccessServer.
     *
     * @param items channels to create and listen to.
     */
    public void initialize(Channels items) {
        for (BaseChannelType item : items.getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            try {
                if (item instanceof HealthChannelType) {
                    addHealthVariable(item.getGiapiname(), item.getEpicsname(), Health.valueOf("BAD"));
                } else if (item instanceof AlarmChannelType) {
                    addAlarmVariable(item.getGiapiname(), item.getEpicsname(), ChannelsHelper.getInitial((SimpleChannelType) item));
                } else if (item instanceof SimpleChannelType) {
                    addVariable(item.getGiapiname(), item.getEpicsname(), ChannelsHelper.getInitial((SimpleChannelType) item));
                }
            } catch (CAException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }


    /**
     * Destroys the registered channels in the IChannelAccessServer
     */
    @Invalidate
    public void shutdown() {
        for (String name : channelMap.keySet().toArray(new String[0])) {
            removeVariable(name);
        }
        for (String name : alarmChannelMap.keySet().toArray(new String[0])) {
            removeVariable(name);
        }
        for (String name : healthChannelMap.keySet().toArray(new String[0])) {
            removeVariable(name);
        }
    }

    private <T> void addVariable(String giapiName, String epicsName, T value) throws CAException {
        if (_channelAccessServer != null) {
            IChannel<T> ch = _channelAccessServer.createChannel(epicsName, value);
            channelMap.put(giapiName, ch);
        } else {
            throw new IllegalStateException("The cas service is unavailable");
        }
        LOG.info("Added variable GIAPI: " + giapiName + " EPICS: " + epicsName);
    }

    private <T> void addAlarmVariable(String giapiName, String epicsName, T value) throws CAException {
        if (_channelAccessServer != null) {
            IAlarmChannel<T> ch = _channelAccessServer.createAlarmChannel(epicsName, value);
            alarmChannelMap.put(giapiName, ch);
        } else {
            throw new IllegalStateException("The cas service is unavailable");
        }
        LOG.info("Added alarm variable GIAPI: " + giapiName + " EPICS: " + epicsName);
    }

    private void addHealthVariable(String giapiName, String epicsName, Health health) throws CAException {
        if (_channelAccessServer != null) {
            IChannel<String> ch = _channelAccessServer.createChannel(epicsName, health.name());
            healthChannelMap.put(giapiName, ch);
        } else {
            throw new IllegalStateException("The cas service is unavailable");
        }
        LOG.info("Added health variable GIAPI: " + giapiName + " EPICS: " + epicsName);
    }

    private void removeVariable(String giapiName) throws IllegalStateException {
        IChannel ch = null;
        if (channelMap.containsKey(giapiName)) {
            ch = channelMap.remove(giapiName);
        } else if (alarmChannelMap.containsKey(giapiName)) {
            ch = alarmChannelMap.remove(giapiName);
        } else if (healthChannelMap.containsKey(giapiName)) {
            ch = healthChannelMap.remove(giapiName);
        }
        if (ch != null) {
            if (_channelAccessServer != null) {
                _channelAccessServer.destroyChannel(ch);
            } else {
                throw new IllegalStateException("The cas service is unavailable");
            }
        } else {
            LOG.warning("Channel " + giapiName + " is not registered.");
        }
    }

    /**
     * Just for testing
     *
     * @return unmodifiable map
     */
    Map<String, IChannel<?>> getChannels() {
        return Collections.unmodifiableMap(channelMap);
    }

    /**
     * Just for testing
     *
     * @return unmodifiable map
     */
    Map<String, IAlarmChannel<?>> getAlarmChannels() {
        return Collections.unmodifiableMap(alarmChannelMap);
    }

    /**
     * Just for testing
     *
     * @return unmodifiable map
     */
    Map<String, IChannel<String>> getHealthChannels() {
        return Collections.unmodifiableMap(healthChannelMap);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    public <T> void update(StatusItem<T> item) {
        try {
            if (channelMap.containsKey(item.getName())) {
                IChannel ch = channelMap.get(item.getName());
                ch.setValue(item.getValue());
            } else if (alarmChannelMap.containsKey(item.getName())) {
                if (item instanceof AlarmStatusItem) {
                    IAlarmChannel ch = alarmChannelMap.get(item.getName());
                    ch.setValue(item.getValue());
                    setAlarm((AlarmStatusItem<T>) item, ch);
                } else {
                    LOG.warning("Received StatusItem that is not an AlarmStatusItem for " + item.getName());
                }
            } else if (healthChannelMap.containsKey(item.getName())) {
                if (item instanceof HealthStatusItem) {
                    IChannel<String> ch = healthChannelMap.get(item.getName());
                    ch.setValue(((HealthStatusItem) item).getHealth().name());
                } else {
                    LOG.warning("Received StatusItem that is not a HealthStatusItem for " + item.getName());
                }
            } else {
                //received a status item not on the config file
                //ignoring it
                LOG.warning("Unknown item " + item.getName());
            }
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);

        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private <T> void setAlarm(AlarmStatusItem<T> item, IAlarmChannel<T> channel) {
        AlarmState state = item.getAlarmState();
        try {
            channel.setAlarm(CAUSE_MAP.get(state.getCause()), SEVERITY_MAP.get(state.getSeverity()), state.getMessage());
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);

        }
    }

}
