package edu.gemini.epics.impl;

import gov.aps.jca.Monitor;
import gov.aps.jca.event.MonitorListener;

public class MonitorListenerPair {
    public final Monitor monitor;
    public final MonitorListener listener;

    public MonitorListenerPair(Monitor monitor, MonitorListener listener) {
        this.monitor = monitor;
        this.listener = listener;
    }
}
