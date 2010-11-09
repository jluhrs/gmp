package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.cas.GiapiCas;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBRType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class EpicsStatusService
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusService {
    public static final Logger LOG = Logger.getLogger(EpicsStatusService.class.getName());
    /**
     * Structure mapping Giapi Status Item -> Epics PV
     */
    private final Map<String, String> channelMap = new HashMap<String, String>();
    private GiapiCas _cas=null;
    public EpicsStatusService(GiapiCas cas) {
        _cas=cas;
    }

    public void addVariable(String giapiName, String epicsName, DBRType type, Object initialValue)throws CAException, IllegalArgumentException, IllegalStateException{
        channelMap.put(giapiName, epicsName);
        if(_cas!=null){
            _cas.addVariable(epicsName,type,initialValue);
        }else{
            throw new IllegalStateException("The giapi-cas service is unavailable");
        }
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
}
