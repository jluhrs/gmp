package edu.gemini.cas;

import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.dbr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class IntegerChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class FloatChannel extends AbstractChannel<Float> {

    FloatChannel(String name, int length) {
        super(new AlarmMemoryProcessVariable(name,null, DBR_Float.TYPE,new float[length]));
    }

    @Override
    protected boolean validateArgument(List<Float> values) {
        try{
            Float a= (Float)values.get(0);
        }catch(ClassCastException ex){
            return false;
        }
        return isFloat() && (getSize() == values.size());
    }

    @Override
    protected DBR buildDBR(List<Float> values) {
        float[] newValues = new float[values.size()];
        for(Float value:values){
            newValues[0] = value;
        }
        return new DBR_STS_Float(newValues);
    }

    @Override
    protected DBR emptyDBR() {
        return new DBR_TIME_Float(getSize());
    }

    @Override
    protected List<Float> extractValues(DBR dbr) {
        List<Float> values=new ArrayList<Float>();
        Object objVal = dbr.getValue();
        float[] floatVal = (float[])objVal;
        for(float a:floatVal){
            values.add(a);
        }
        return values;
    }
}
