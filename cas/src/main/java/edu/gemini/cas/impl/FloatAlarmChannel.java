package edu.gemini.cas.impl;

/**
 * Class FloatAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class FloatAlarmChannel extends AbstractAlarmChannel<Float> {
    FloatAlarmChannel(String name, int length) {
        super(new FloatChannel(name,length));
    }

}
