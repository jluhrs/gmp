package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import edu.gemini.epics.ClientEpicsChannel;
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
 * Class EpicsChannel
 *
 * @author Nicolas A. Barriga
 *         Date: 11/7/11
 */
public class EpicsChannel<T> implements ClientEpicsChannel<T> {
    private final CAJChannel channel;
    private final Map<ChannelListener<?>, Tuple2<Monitor, MonitorListener>> listeners = new HashMap<ChannelListener<?>, Tuple2<Monitor, MonitorListener>>();

    public EpicsChannel(CAJChannel channel) {
        this.channel = channel;
    }

    @Override
    public DBR getDBR() throws CAException {
        DBR dbr = channel.get();
        try {
            channel.getContext().pendIO(1.0);
        } catch (TimeoutException e) {
            throw new CAException(e);
        }
        return dbr;
    }

    @Override
    public List<T> getAll() throws CAException {
        return (List<T>) DbrUtil.extractValues(getDBR());
    }

    @Override
    public T getFirst() throws CAException {
        return (T) (DbrUtil.extractValues(getDBR()).get(0));
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    @Override
    public void registerListener(final ChannelListener<T> tChannelListener) {
        try {
            MonitorListener ml = new MonitorListener() {
                @Override
                public void monitorChanged(MonitorEvent ev) {
                    tChannelListener.valueChanged(channel.getName(), (List<T>) DbrUtil.extractValues(ev.getDBR()));
                }
            };
            listeners.put(tChannelListener, new Pair<Monitor, MonitorListener>(channel.addMonitor(Monitor.VALUE, ml), ml));
        } catch (CAException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegisterListener(ChannelListener<T> tChannelListener) {
        Monitor mon = listeners.get(tChannelListener)._1();
        MonitorListener ml = listeners.get(tChannelListener)._2();
        mon.removeMonitorListener(ml);
        if (mon.getMonitorListeners().length == 0) {
            try {
                mon.clear();
            } catch (CAException e) {
                throw new RuntimeException(e);
            }
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
    public void destroy() {
        try {
            channel.destroy();
        } catch (CAException e) {
            throw new RuntimeException(e);
        }
    }
}
