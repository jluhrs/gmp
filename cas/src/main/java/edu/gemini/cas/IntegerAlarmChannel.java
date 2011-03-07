package edu.gemini.cas;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.DBR_String;

/**
 * Class IntegerAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
public class IntegerAlarmChannel extends AbstractAlarmChannel<Integer> {
    IntegerAlarmChannel(String name, int length) {
        super(new IntegerChannel(name,length), new AlarmMemoryProcessVariable(name+".OMSS",null, DBR_String.TYPE,new String[]{""}));
    }

}
