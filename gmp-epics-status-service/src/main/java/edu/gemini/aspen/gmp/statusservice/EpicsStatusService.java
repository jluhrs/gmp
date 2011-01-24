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
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
//TODO:support multiple values (array)
@Component(name = "GMP_EPICS_STATUS_SERVICE", managedservice = "edu.gemini.aspen.gmp.statusservice.EpicsStatusService")
@Instantiate(name = "GMP_EPICS_STATUS_SERVICE")
@Provides
public class EpicsStatusService implements StatusHandler {
    public static final Logger LOG = Logger.getLogger(EpicsStatusService.class.getName());
    public static final Map<AlarmCause, Status> CAUSE_MAP = new HashMap<AlarmCause, Status>();
    public static final Map<AlarmSeverity, Severity> SEVERITY_MAP = new HashMap<AlarmSeverity, Severity>();

    static{
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_OK,Status.NO_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_HIHI,Status.HIHI_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_HI,Status.HIGH_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_LO,Status.LOW_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_LOLO,Status.LOLO_ALARM);
        CAUSE_MAP.put(AlarmCause.ALARM_CAUSE_OTHER,Status.SOFT_ALARM);//TODO: fix this mapping

        SEVERITY_MAP.put(AlarmSeverity.ALARM_OK,Severity.NO_ALARM);
        SEVERITY_MAP.put(AlarmSeverity.ALARM_WARNING,Severity.MINOR_ALARM);
        SEVERITY_MAP.put(AlarmSeverity.ALARM_FAILURE,Severity.MAJOR_ALARM);
    }

    private static final String NAME = "GMP_EPICS_STATUS_SERVICE";
    /**
     * Structure mapping Giapi Status Item -> Epics PV
     */
    private final Map<String, IChannel> channelMap = new HashMap<String, IChannel>();
    private final Map<String, IAlarmChannel> alarmChannelMap = new HashMap<String, IAlarmChannel>();
    private final Map<String, IChannel> healthChannelMap = new HashMap<String, IChannel>();

    @Requires
    private IChannelAccessServer _channelAccessServer;

    @Property(name = "xmlFileName", value="INVALID", mandatory = true)
    private String xmlFileName;
    @Property(name = "xsdFileName", value="INVALID", mandatory = true)
    private String xsdFileName;

    public EpicsStatusService(){

    }

    public EpicsStatusService(IChannelAccessServer ICas) {
        _channelAccessServer = ICas;
    }


    @Validate
    public void initialize(){
        EpicsStatusServiceConfiguration conf=  new EpicsStatusServiceConfiguration(xmlFileName, xsdFileName);
        initialize(conf.getSimulatedChannels());
    }
    /**
     * Initialize the EpicsStatusService. Creates appropriate channels in an IChannelAccessServer.
     *
     *
     * @param items channels to create and listen to.
     */
    public void initialize(Channels items) {
        for (BaseChannelType item : items.getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            if (item instanceof HealthChannelType) {
                try {
                    addHealthVariable(item.getGiapiname(), item.getEpicsname(), Health.valueOf("BAD"));
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }else if (item instanceof AlarmChannelType) {
                try {
                    addAlarmVariable(item.getGiapiname(), item.getEpicsname(), ChannelsHelper.getInitial((SimpleChannelType) item));
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            } else if (item instanceof SimpleChannelType) {
                try {
                    addVariable(item.getGiapiname(), item.getEpicsname(), ChannelsHelper.getInitial((SimpleChannelType) item));
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Initialize method version for testing only.
     *
     *
     * @param items channels to create and listen to.
     */
    public void testInitialize(Channels items) {
        for (BaseChannelType item : items.getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            if (item instanceof HealthChannelType) {
                healthChannelMap.put(item.getGiapiname(), null);
            } else if (item instanceof AlarmChannelType) {
                alarmChannelMap.put(item.getGiapiname(), null);
            } else if (item instanceof SimpleChannelType) {
                channelMap.put(item.getGiapiname(), null);
            }
        }
    }


    /**
     * Destroys the registered channels in the IChannelAccessServer
     */
    @Invalidate
    public void shutdown(){
        for(String name: channelMap.keySet().toArray(new String[0])){
            removeVariable(name);
        }
        for(String name: alarmChannelMap.keySet().toArray(new String[0])){
            removeVariable(name);
        }
        for(String name: healthChannelMap.keySet().toArray(new String[0])){
            removeVariable(name);
        }
    }

    private <T> void addVariable(String giapiName, String epicsName, T value)throws CAException{
//        LOG.info("Adding variable GIAPI: "+giapiName+" EPICS: "+epicsName);
        if(_channelAccessServer !=null){
            IChannel ch;
            if(value.getClass() == Integer.class){
                ch = _channelAccessServer.createIntegerChannel(epicsName, 1);
                ch.setValue((Integer)value);
                channelMap.put(giapiName, ch);
            }else if(value.getClass() == Float.class){
                ch = _channelAccessServer.createFloatChannel(epicsName, 1);
                ch.setValue((Float)value);
                channelMap.put(giapiName, ch);
            }else if(value.getClass() == Double.class){
                ch = _channelAccessServer.createDoubleChannel(epicsName,  1);
                ch.setValue((Double)value);
                channelMap.put(giapiName, ch);
            }else if(value.getClass() == String.class){
                ch = _channelAccessServer.createStringChannel(epicsName,  1);
                ch.setValue((String)value);
                channelMap.put(giapiName, ch);
            }else{
                throw new IllegalArgumentException("Unsupported item type "+value.getClass());
            }
        }else{
            throw new IllegalStateException("The cas service is unavailable");
        }
        LOG.info("Added variable GIAPI: "+giapiName+" EPICS: "+epicsName);
    }

        private <T> void addAlarmVariable(String giapiName, String epicsName, T value)throws CAException{
//        LOG.info("Adding variable GIAPI: "+giapiName+" EPICS: "+epicsName);
        if(_channelAccessServer !=null){
            IAlarmChannel ch;
            if(value.getClass() == Integer.class){
                ch = _channelAccessServer.createIntegerAlarmChannel(epicsName, 1);
                ch.setValue((Integer)value);
            }else if(value.getClass() == Float.class){
                ch = _channelAccessServer.createFloatAlarmChannel(epicsName, 1);
                ch.setValue((Float)value);
            }else if(value.getClass() == Double.class){
                ch = _channelAccessServer.createDoubleAlarmChannel(epicsName, 1);
                ch.setValue((Double)value);
            }else if(value.getClass() == String.class){
                ch = _channelAccessServer.createStringAlarmChannel(epicsName, 1);
                ch.setValue((String)value);
            }else{
                throw new IllegalArgumentException("Unsupported item type "+value.getClass());
            }
            alarmChannelMap.put(giapiName, ch);
        }else{
            throw new IllegalStateException("The cas service is unavailable");
        }
        LOG.info("Added alarm variable GIAPI: " + giapiName + " EPICS: " + epicsName);
    }

    private void addHealthVariable(String giapiName, String epicsName, Health health)throws CAException{
    if(_channelAccessServer !=null){
        IChannel ch= _channelAccessServer.createStringChannel(epicsName,1);
        ch.setValue(health.name());
        healthChannelMap.put(giapiName, ch);
    }else{
        throw new IllegalStateException("The cas service is unavailable");
    }
    LOG.info("Added health variable GIAPI: " + giapiName + " EPICS: " + epicsName);
    }

    private void removeVariable(String giapiName) throws IllegalStateException {
        IChannel ch=null;
        if (channelMap.containsKey(giapiName)) {
            ch = channelMap.get(giapiName);
            channelMap.remove(giapiName);
        } else if (alarmChannelMap.containsKey(giapiName)) {
            ch = alarmChannelMap.get(giapiName);
            alarmChannelMap.remove(giapiName);
        } else if (healthChannelMap.containsKey(giapiName)) {
            ch = healthChannelMap.get(giapiName);
            healthChannelMap.remove(giapiName);
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
     */
    public void dump(){
        LOG.info(channelMap.toString());
        LOG.info(alarmChannelMap.toString());
        LOG.info(healthChannelMap.toString());
    }

    /**
     * Just for testing
     * @return unmodifiable map
     */
    public Map<String, IChannel> getChannels(){
        return Collections.unmodifiableMap(channelMap);
    }
    /**
     * Just for testing
     * @return unmodifiable map
     */
    public Map<String, IAlarmChannel> getAlarmChannels(){
        return Collections.unmodifiableMap(alarmChannelMap);
    }
    /**
     * Just for testing
     * @return unmodifiable map
     */
    public Map<String, IChannel> getHealthChannels(){
        return Collections.unmodifiableMap(healthChannelMap);
    }
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void update(StatusItem item) {
//        LOG.info("Update item "+item.getName());
        updateInternal(item);
    }

    //StatusHandler.update() should be a generic method
    private <T> void updateInternal(StatusItem<T> item) {
        if (channelMap.containsKey(item.getName())) {
            IChannel ch = channelMap.get(item.getName());
            if (_channelAccessServer != null) {
                try {
                    if(item.getValue().getClass() == Integer.class){
                        ch.setValue((Integer)item.getValue());
                    }else if(item.getValue().getClass() == Float.class){
                        ch.setValue((Float)item.getValue());
                    }else if(item.getValue().getClass() == Double.class){
                        ch.setValue((Double)item.getValue());
                    }else if(item.getValue().getClass() == String.class){
                        ch.setValue((String)item.getValue());
                    }else{
                        LOG.warning("Unsupported item type "+item.getValue().getClass());
                    }
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(),ex);

                } catch (IllegalArgumentException ex) {
                   LOG.log(Level.SEVERE, ex.getMessage(),ex);
                }
            }
        }else if (alarmChannelMap.containsKey(item.getName())) {
            if (item instanceof AlarmStatusItem) {
                IAlarmChannel ch = alarmChannelMap.get(item.getName());
                if (_channelAccessServer != null) {
                    try {
                        if (item.getValue().getClass() == Integer.class) {
                            ch.setValue((Integer) item.getValue());
                            setAlarm((AlarmStatusItem<T>)item,ch);
                        } else if (item.getValue().getClass() == Float.class) {
                            ch.setValue((Float) item.getValue());
                            setAlarm((AlarmStatusItem<T>)item,ch);
                        } else if (item.getValue().getClass() == Double.class) {
                            ch.setValue((Double) item.getValue());
                            setAlarm((AlarmStatusItem<T>)item,ch);
                        } else if (item.getValue().getClass() == String.class) {
                            ch.setValue((String) item.getValue());
                            setAlarm((AlarmStatusItem<T>)item,ch);
                        } else {
                            LOG.warning("Unsupported item type " + item.getValue().getClass());
                        }
                    } catch (CAException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);

                    } catch (IllegalArgumentException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }else{
                LOG.warning("Received StatusItem that is not an AlarmStatusItem for "+item.getName());
            }
        }else if (healthChannelMap.containsKey(item.getName())) {
            if (item instanceof HealthStatusItem) {
                IChannel ch = healthChannelMap.get(item.getName());
                if (_channelAccessServer != null) {
                    try {
                        switch(((HealthStatusItem)item).getHealth()){
                            case GOOD:
                                ch.setValue("GOOD");
                                break;
                            case WARNING:
                                ch.setValue("WARNING");
                                break;
                            case BAD:
                                ch.setValue("BAD");
                                break;
                        }
                    } catch (CAException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);

                    } catch (IllegalArgumentException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }else{
                LOG.warning("Received StatusItem that is not a HealthStatusItem for "+item.getName());
            }
        }else{
            //received a status item not on the config file
            //ignoring it
            LOG.warning("Unknown item "+item.getName());
        }
    }

    private <T> void setAlarm(AlarmStatusItem<T> item, IAlarmChannel channel){
        AlarmState state = item.getAlarmState();
        try{
            channel.setAlarm(CAUSE_MAP.get(state.getCause()),SEVERITY_MAP.get(state.getSeverity()),state.getMessage());
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);

        }
    }

}
