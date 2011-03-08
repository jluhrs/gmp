package edu.gemini.cas.impl;

/**
 * Class DoubleAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class DoubleAlarmChannel extends AbstractAlarmChannel<Double> {
    DoubleAlarmChannel(String name, int length) {
        super(new DoubleChannel(name,length));
    }

}
