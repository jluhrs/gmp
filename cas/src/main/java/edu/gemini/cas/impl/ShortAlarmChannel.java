package edu.gemini.cas.impl;

public class ShortAlarmChannel extends AbstractAlarmChannel<Short> {
    ShortAlarmChannel(String name, int length) {
        super(new ShortChannel(name,length));
    }

}
