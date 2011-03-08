package edu.gemini.cas;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.DBR_String;

/**
 * Class IntegerAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class DoubleAlarmChannel extends AbstractAlarmChannel<Double> {
    DoubleAlarmChannel(String name, int length) {
        super(new DoubleChannel(name,length));
    }

}
