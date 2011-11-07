package edu.gemini.aspen.gmp.epics.impl;

import com.google.common.collect.ImmutableList;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import edu.gemini.aspen.gmp.epics.EpicsUpdateListener;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EpicsUpdaterThreadTest {
    private String channel = "X.val1";
    private EpicsUpdate<Integer> epicsUpdate = new EpicsUpdateImpl<Integer>(channel, ImmutableList.of(1));
    private CountDownLatch latch = new CountDownLatch(1);
    private AtomicBoolean passed = new AtomicBoolean(false);

    @Test
    public void testUpdateOnRegisteredListener() throws InterruptedException {
        EpicsUpdaterThread epicsUpdater = new EpicsUpdaterThread();
        try {
            EpicsUpdateListener listener = createListener();

            epicsUpdater.registerInterest(channel, listener);
            epicsUpdater.start();
            epicsUpdater.processEpicsUpdate(epicsUpdate);

            // Wait on the latch for up to 2 secs;
            latch.await(2000, TimeUnit.MILLISECONDS);
            assertTrue(passed.get());
        } finally {
            epicsUpdater.stop();
        }
    }

    private EpicsUpdateListener createListener() {
        return new EpicsUpdateListener() {
            @Override
            public void onEpicsUpdate(EpicsUpdate<?> update) {
                passed.set(epicsUpdate.equals(update));
                latch.countDown();
            }
        };
    }

    @Test
    public void testNoUpdateOnUnRegisteredListener() throws InterruptedException {
        EpicsUpdaterThread epicsUpdater = new EpicsUpdaterThread();
        try {
            EpicsUpdateListener listener = createListener();

            epicsUpdater.registerInterest(channel, listener);
            epicsUpdater.start();
            epicsUpdater.unregisterInterest(channel);
            epicsUpdater.processEpicsUpdate(epicsUpdate);

            // Wait on the latch for just 200 milisecs;
            latch.await(100, TimeUnit.MILLISECONDS);
            assertFalse(passed.get());
        } finally {
            epicsUpdater.stop();
        }
    }

    @Test
    public void testNoUpdateOnOtherChannelListener() throws InterruptedException {
        EpicsUpdaterThread epicsUpdater = new EpicsUpdaterThread();
        try {
            EpicsUpdateListener listener = createListener();

            epicsUpdater.registerInterest("X.val2", listener);
            epicsUpdater.start();
            epicsUpdater.processEpicsUpdate(epicsUpdate);

            // Wait on the latch for just 200 milisecs;
            latch.await(100, TimeUnit.MILLISECONDS);
            assertFalse(passed.get());
        } finally {
            epicsUpdater.stop();
        }
    }
}
