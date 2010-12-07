package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.gmp.statusservice.osgi.EpicsStatusServiceConfiguration;
import edu.gemini.cas.IChannel;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.cas.IChannelAccessServer;
import edu.gemini.cas.IChannelFactory;
import gov.aps.jca.CAException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsStatusService
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusService implements StatusHandler {
    public static final Logger LOG = Logger.getLogger(EpicsStatusService.class.getName());
    private static final String NAME = "GMP_EPICS_STATUS_SERVICE";
    /**
     * Structure mapping Giapi Status Item -> Epics PV
     */
    private final Map<String, IChannel> channelMap = new HashMap<String, IChannel>();
    private IChannelAccessServer _channelAccessServer =null;
    public EpicsStatusService(IChannelAccessServer ICas) {
        _channelAccessServer = ICas;
    }

    //TODO:support multiple values (array)
    public void initialize(Set<EpicsStatusServiceConfiguration.StatusConfigItem> items){
        for(EpicsStatusServiceConfiguration.StatusConfigItem item: items){
            try{
                addVariable(item.giapiName, item.epicsName, item.initialValue);
            }catch(CAException ex){
                LOG.log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

    public void shutdown(){
        for(String name:channelMap.keySet()){
            removeVariable(name);
        }
    }

    private <T> void addVariable(String giapiName, String epicsName, T value)throws CAException{
        LOG.info("Adding variable GIAPI: "+giapiName+" EPICS: "+epicsName);
        if(_channelAccessServer !=null){
            IChannel ch;
            if(value.getClass() == Integer.class){
                ch = _channelAccessServer.createIntegerChannel(epicsName, 1);
                ch.setValue((Integer)value);
            }else if(value.getClass() == Float.class){
                ch = _channelAccessServer.createFloatChannel(epicsName, 1);
                ch.setValue((Float)value);
            }else if(value.getClass() == Double.class){
                ch = _channelAccessServer.createDoubleChannel(epicsName,  1);
                ch.setValue((Double)value);
            }else if(value.getClass() == String.class){
                ch = _channelAccessServer.createStringChannel(epicsName,  1);
                ch.setValue((String)value);
            }else{
                throw new IllegalArgumentException("Unsupported item type "+value.getClass());
            }
            channelMap.put(giapiName, ch);
        }else{
            throw new IllegalStateException("The giapi-cas service is unavailable");
        }
        LOG.info("Added variable GIAPI: "+giapiName+" EPICS: "+epicsName);
    }

    private void removeVariable(String giapiName)throws IllegalStateException{
        IChannel ch = channelMap.get(giapiName);
        if(ch != null){
            channelMap.remove(giapiName);
            if(_channelAccessServer !=null){
                _channelAccessServer.destroyChannel(ch);
            }else{
                throw new IllegalStateException("The giapi-cas service is unavailable");
            }
        }
    }

    /**
     * Just for testing
     */
    public void dump(){
           LOG.info(channelMap.toString());
    }

    /**
     * Just for testing
     * @return unmodifiable map
     */
    public Map<String, IChannel> getAll(){
        return Collections.unmodifiableMap(channelMap);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void update(StatusItem item) {
        LOG.info("Update item "+item.getName());
        updateInternal(item);
    }

    //StatusHandler.update() should be a generic method
    private <T> void updateInternal(StatusItem<T> item) {
        if (channelMap.containsKey(item.getName())) {
            if (_channelAccessServer != null) {
                try {
                    if(item.getValue().getClass() == Integer.class){
                        channelMap.get(item.getName()).setValue((Integer)item.getValue());
                    }else if(item.getValue().getClass() == Float.class){
                        channelMap.get(item.getName()).setValue((Float)item.getValue());
                    }else if(item.getValue().getClass() == Double.class){
                        channelMap.get(item.getName()).setValue((Double)item.getValue());
                    }else if(item.getValue().getClass() == String.class){
                        channelMap.get(item.getName()).setValue((String)item.getValue());
                    }else{
                        LOG.warning("Unsupported item type "+item.getValue().getClass());
                    }
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE,"",ex);

                } catch (IllegalArgumentException ex) {
                   LOG.log(Level.SEVERE,"",ex);
                }
            }
        }else{
            //received a status item not on the config file
            //ignoring it
            LOG.warning("Unknown item "+item.getName());
        }
    }
}
