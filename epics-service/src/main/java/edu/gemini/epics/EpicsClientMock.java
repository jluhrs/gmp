package edu.gemini.epics;

import edu.gemini.epics.api.EpicsClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of EpicsClient used for testing.
 * This class should live in the test section of the code but
 * due to limitation of the testing framework it needs to be
 * in the main code tree
 * <p/>
 * The client can receive updates and it record if it has been
 * called
 */
public class EpicsClientMock implements EpicsClient {
    private boolean connectedCalled = false;
    private boolean disconnectedCalled = false;
    private AtomicInteger updatesCount = new AtomicInteger(0);

    @Override
    public <T> void valueChanged(String channel, List<T> values) {
        updatesCount.incrementAndGet();
    }

    @Override
    public void connected() {
        connectedCalled = true;
    }

    @Override
    public void disconnected() {
        disconnectedCalled = true;
    }

    public boolean wasConnectedCalled() {
        return connectedCalled;
    }

    public boolean wasDisconnectedCalled() {
        return disconnectedCalled;
    }

    public int getUpdatesCount() {
        return updatesCount.get();
    }
}
