package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.cas.GiapiCas;
import edu.gemini.cas.IGiapiCas;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import gov.aps.jca.CAException;

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
    private IGiapiCas _cas=null;
    public EpicsStatusService(GiapiCas cas) {
        _cas=cas;
    }

    public <T> void addVariable(String giapiName, String epicsName, T firstValue, T... remainingValues)throws CAException, IllegalArgumentException, IllegalStateException{
        LOG.info("Adding variable GIAPI: "+giapiName+" EPICS: "+epicsName);
        channelMap.put(giapiName, epicsName);
        if(_cas!=null){
            _cas.addVariable(epicsName, firstValue, remainingValues);
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
                    _cas.put(channelMap.get(item.getName()), item.getValue());
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
