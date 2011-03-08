package edu.gemini.cas.impl;

/**
 * Class StringAlarmChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 3/7/11
 */
class StringAlarmChannel extends AbstractAlarmChannel<String> {
    StringAlarmChannel(String name, int length) {
        super(new StringChannel(name,length));
    }

}
