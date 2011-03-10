package edu.gemini.epics.impl;

import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChannelBindingSupportTest {
    private static final String CHANNEL_NAME = "tst:tst";
    private Context context = mock(Context.class);

    @Test
    public void testBindingChannel() throws CAException {
        Context context = mock(Context.class);
        IEpicsClient target = mock(IEpicsClient.class);
        ChannelBindingSupport cbs = new ChannelBindingSupport(context, target);

        cbs.bindChannel(CHANNEL_NAME);

        verify(context).createChannel(eq(CHANNEL_NAME), any(ConnectionListener.class));
    }

    @Test
    public void testConnectionChangedEventOnConnection() throws CAException {
        IEpicsClient target = mock(IEpicsClient.class);
        ChannelBindingSupport cbs = new ChannelBindingSupport(context, target);

        cbs.bindChannel(CHANNEL_NAME);

        Channel channel = mock(Channel.class);
        ConnectionListener listener = mockConnectionListener(channel);
        listener.connectionChanged(new ConnectionEvent(channel, true));

        // assert that a channel monitor is added
        verify(channel).addMonitor(eq(Monitor.VALUE), any(MonitorListener.class));
    }

    private ConnectionListener mockConnectionListener(Channel channel) throws CAException {
        // Capture the connection listener to simulate the event
        ArgumentCaptor<ConnectionListener> connectionListenerArgument = ArgumentCaptor.forClass(ConnectionListener.class);
        verify(context).createChannel(eq(CHANNEL_NAME), connectionListenerArgument.capture());

        ConnectionListener listener = connectionListenerArgument.getValue();
        // Simulate a connection change event

        when(channel.getContext()).thenReturn(context);
        return listener;
    }

    @Test
    public void testConnectionChangedEventOnDisconnection() throws CAException {
        IEpicsClient target = mock(IEpicsClient.class);
        ChannelBindingSupport cbs = new ChannelBindingSupport(context, target);

        cbs.bindChannel(CHANNEL_NAME);

        Channel channel = mock(Channel.class);
        when(channel.getContext()).thenReturn(context);
        when(channel.getName()).thenReturn(CHANNEL_NAME);

        Channel replacingChannel = mock(Channel.class);
        when(replacingChannel.getContext()).thenReturn(context);

        when(context.createChannel(eq(CHANNEL_NAME), any(ConnectionListener.class))).thenReturn(replacingChannel);

        ConnectionListener listener = mockConnectionListener(channel);
        listener.connectionChanged(new ConnectionEvent(channel, false));

        verify(target).channelChanged(CHANNEL_NAME, null);
    }

    @Test
    public void testChannelChange() throws CAException {
        IEpicsClient target = mock(IEpicsClient.class);
        ChannelBindingSupport cbs = new ChannelBindingSupport(context, target);

        cbs.bindChannel(CHANNEL_NAME);

        Channel channel = mock(Channel.class);
        when(channel.getName()).thenReturn(CHANNEL_NAME);

        ConnectionListener listener = mockConnectionListener(channel);
        ArgumentCaptor<MonitorListener> monitorListenerArgument = ArgumentCaptor.forClass(MonitorListener.class);
        listener.connectionChanged(new ConnectionEvent(channel, true));
        
        verify(channel).addMonitor(eq(Monitor.VALUE), monitorListenerArgument.capture());

        // Simulate an update sent through a MonitorListener
        MonitorListener monitorListener = monitorListenerArgument.getValue();

        when(channel.getConnectionState()).thenReturn(Channel.ConnectionState.CONNECTED);

        DBR dbr = new DBR_Float(1);
        CAStatus status = CAStatus.NORMAL;
        MonitorEvent event = new MonitorEvent(channel, dbr, status);
        monitorListener.monitorChanged(event);

        // Need to capture the get call too
        ArgumentCaptor<GetListener> getListenerArgument = ArgumentCaptor.forClass(GetListener.class);
        listener.connectionChanged(new ConnectionEvent(channel, true));

        verify(channel).get(getListenerArgument.capture());

        GetListener listenerOfGets = getListenerArgument.getValue();
        GetEvent getEvent = new GetEvent(channel, dbr, status);
        listenerOfGets.getCompleted(getEvent);

        // assert that a channel monitor is added
        verify(target).channelChanged(eq(CHANNEL_NAME), eq(dbr.getValue()));

    }
}
