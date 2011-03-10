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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChannelBindingSupportTest {
    private static final String CHANNEL_NAME = "tst:tst";
    private Context context;
    private IEpicsClient epicsClient;
    private ChannelBindingSupport cbs;
    private Channel channel;

    @Before
    public void setUp() throws Exception {
        epicsClient = mock(IEpicsClient.class);
        context = mock(Context.class);
        cbs = new ChannelBindingSupport(context, epicsClient);

        channel = mock(Channel.class);
        when(channel.getContext()).thenReturn(context);
        when(channel.getName()).thenReturn(CHANNEL_NAME);
    }

    @Test
    public void testBindingChannel() throws CAException {
        cbs.bindChannel(CHANNEL_NAME);

        verify(context).createChannel(eq(CHANNEL_NAME), any(ConnectionListener.class));
    }

    @Test
    public void testConnectionChangedEventOnConnection() throws CAException {
        cbs.bindChannel(CHANNEL_NAME);

        simulateConnectionStarted();

        // assert that a channel monitor is added
        verify(channel).addMonitor(eq(Monitor.VALUE), any(MonitorListener.class));
    }

    private ConnectionListener simulateConnectionStarted() throws CAException {
        ConnectionListener listener = mockConnectionListener(channel);
        // Simulate a connection change event
        listener.connectionChanged(new ConnectionEvent(channel, true));
        return listener;
    }

    private ConnectionListener mockConnectionListener(Channel channel) throws CAException {
        // Capture the connection listener to simulate the event
        ArgumentCaptor<ConnectionListener> connectionListenerArgument = ArgumentCaptor.forClass(ConnectionListener.class);
        verify(context).createChannel(eq(CHANNEL_NAME), connectionListenerArgument.capture());

        ConnectionListener listener = connectionListenerArgument.getValue();

        when(channel.getContext()).thenReturn(context);
        return listener;
    }

    @Test
    public void testConnectionChangedEventOnDisconnection() throws CAException {
        cbs.bindChannel(CHANNEL_NAME);

        Channel replacingChannel = mock(Channel.class);
        when(replacingChannel.getContext()).thenReturn(context);

        when(context.createChannel(eq(CHANNEL_NAME), any(ConnectionListener.class))).thenReturn(replacingChannel);

        ConnectionListener listener = mockConnectionListener(channel);
        listener.connectionChanged(new ConnectionEvent(channel, false));

        verify(epicsClient).channelChanged(CHANNEL_NAME, null);
    }

    @Test
    public void testChannelChange() throws CAException {
        cbs.bindChannel(CHANNEL_NAME);
        // Needed when distributing updates
        when(channel.getConnectionState()).thenReturn(Channel.ConnectionState.CONNECTED);

        ConnectionListener connectionListener = simulateConnectionStarted();

        MonitorListener monitorListener = buildMockMonitorListener();

        DBR dbr = new DBR_Float(1);
        CAStatus status = CAStatus.NORMAL;
        MonitorEvent event = new MonitorEvent(channel, dbr, status);

        // Simulate monitor event
        monitorListener.monitorChanged(event);

        // Need to capture the getListener too
        ArgumentCaptor<GetListener> getListenerArgument = ArgumentCaptor.forClass(GetListener.class);
        connectionListener.connectionChanged(new ConnectionEvent(channel, true));
        verify(channel).get(getListenerArgument.capture());

        // Simulate a getCompleted event
        GetListener listenerOfGets = getListenerArgument.getValue();
        GetEvent getEvent = new GetEvent(channel, dbr, status);
        listenerOfGets.getCompleted(getEvent);

        // assert that a channel changed event is sent
        verify(epicsClient).channelChanged(eq(CHANNEL_NAME), eq(dbr.getValue()));

    }

    private MonitorListener buildMockMonitorListener() throws CAException {
        ArgumentCaptor<MonitorListener> monitorListenerArgument = ArgumentCaptor.forClass(MonitorListener.class);
        verify(channel).addMonitor(eq(Monitor.VALUE), monitorListenerArgument.capture());

        // Simulate an update sent through a MonitorListener
        return monitorListenerArgument.getValue();
    }
}
