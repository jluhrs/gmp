package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.api.DbrUtil;
import edu.gemini.shared.util.immutable.Pair;
import edu.gemini.shared.util.immutable.Tuple2;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Monitor;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class ReadOnlyEpicsChannelImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 11/7/11
 */
public class ReadOnlyEpicsChannelImpl<T> implements ReadOnlyClientEpicsChannel<T> {
    protected final CAJChannel channel;
    private final Map<ChannelListener<?>, Tuple2<Monitor, MonitorListener>> listeners = new HashMap<ChannelListener<?>, Tuple2<Monitor, MonitorListener>>();

    public ReadOnlyEpicsChannelImpl(CAJChannel channel) {
        this.channel = channel;
    }

    @Override
    public DBR getDBR() throws CAException, TimeoutException {
        DBR dbr = channel.get();
        channel.getContext().pendIO(1.0);
        return dbr;
    }

    @Override
    public List<T> getAll() throws CAException, TimeoutException {
        return (List<T>) DbrUtil.extractValues(getDBR());
    }

    @Override
    public T getFirst() throws CAException, TimeoutException {
        return getAll().get(0);
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    @Override
    public synchronized void registerListener(final ChannelListener<T> tChannelListener) throws CAException {
        MonitorListener ml = new MonitorListener() {
            @Override
            public void monitorChanged(MonitorEvent ev) {
                tChannelListener.valueChanged(channel.getName(), (List<T>) DbrUtil.extractValues(ev.getDBR()));
            }
        };
        listeners.put(tChannelListener, new Pair<Monitor, MonitorListener>(channel.addMonitor(Monitor.VALUE, ml), ml));
    }

    @Override
    public synchronized void unRegisterListener(ChannelListener<T> tChannelListener) throws CAException {
        Monitor mon = listeners.get(tChannelListener)._1();
        MonitorListener ml = listeners.get(tChannelListener)._2();
        mon.removeMonitorListener(ml);
        if (mon.getMonitorListeners().length == 0) {
            mon.clear();
        }
    }

    @Override
    public boolean isValid() {
        return channel.getConnectionState() == Channel.ConnectionState.CONNECTED;
    }

    @Override
    public DBRType getType() {
        return channel.getFieldType();
    }

    @Override
    public void destroy() throws CAException {
        channel.destroy();
    }
}
