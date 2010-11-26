package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.cas.GiapiCas;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBRType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, String> channelMap = new HashMap<String, String>();
    private GiapiCas _cas=null;
    public EpicsStatusService(GiapiCas cas) {
        _cas=cas;
    }

    public void addVariable(String giapiName, String epicsName, DBRType type, Object initialValue)throws CAException, IllegalArgumentException, IllegalStateException{
        LOG.info("Adding variable GIAPI: "+giapiName+" EPICS: "+epicsName);
        channelMap.put(giapiName, epicsName);
        if(_cas!=null){
            _cas.addVariable(epicsName,type,initialValue);
        }else{
            throw new IllegalStateException("The giapi-cas service is unavailable");
        }
        LOG.info("Added variable GIAPI: "+giapiName+" EPICS: "+epicsName);
    }

    public void removeVariable(String giapiName)throws IllegalStateException{
        String epicsName = channelMap.get(giapiName);
        if(epicsName != null){
            channelMap.remove(giapiName);
            if(_cas!=null){
                _cas.removeVariable(epicsName);
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
    public Map<String,String> getAll(){
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
            if (_cas != null) {
                try {
                    T value = item.getValue();
                    String epicsname=channelMap.get(item.getName());
                    if (value.getClass() == Integer.class) {
                        _cas.put(epicsname, (Integer) value);
                    }else if (value.getClass() == Double.class) {
                        _cas.put(epicsname, (Double) value);
                    }else if (value.getClass() == Float.class) {
                        _cas.put(epicsname, (Float) value);
                    }else if (value.getClass() == String.class) {
                        _cas.put(epicsname, (String) value);
                    }else{
                        //StatusItem type was not one of the 4 supported types
                        LOG.warning("Unsupported item type "+value.getClass());

                    }
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE,"",ex);
                } catch (IllegalStateException ex) {
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
