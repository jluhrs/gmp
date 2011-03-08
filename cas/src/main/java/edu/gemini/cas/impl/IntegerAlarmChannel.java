package edu.gemini.cas.impl;

/**
 * Class IntegerAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class IntegerAlarmChannel extends AbstractAlarmChannel<Integer> {
    IntegerAlarmChannel(String name, int length) {
        super(new IntegerChannel(name,length));
    }

}
