package edu.gemini.cas.impl;

/**
 * Class EnumAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/17/11
 */
public class EnumAlarmChannel<T extends Enum<T>> extends AbstractAlarmChannel<T>{
    protected EnumAlarmChannel(String name, int length, Class<T> clazz) {
        super(new EnumChannel<T>(name,length,clazz));
    }
    Class<T> getEnumClass(){
        return ((EnumChannel<T>)ch).getEnumClass();
    }
}
