package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class ChannelSimulatorTest {
    private static final int PASSES = 10;

    @Test
    public void testRunningSimulator() throws InterruptedException {
        ExecutorService executorService =
                Executors.newFixedThreadPool(1);
        long updateRate = 10L;
        String channelName = "channel";
        int size = 5;
        SimulatedEpicsChannel channel = SimulatedEpicsChannel.buildSimulatedEpicsChannel(channelName, size, DataType.INT, updateRate);
        EpicsRegistrar registrar = mock(EpicsRegistrar.class);
        ChannelSimulator simulator = new ChannelSimulator(channel, registrar);
        executorService.submit(simulator);

        synchronized (this) {
            TimeUnit.MILLISECONDS.sleep(PASSES * updateRate);
        }

        verify(registrar, atLeast(PASSES - 1)).processEpicsUpdate(Matchers.<EpicsUpdate>anyObject());
    }
}
