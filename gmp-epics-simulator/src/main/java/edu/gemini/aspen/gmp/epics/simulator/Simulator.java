package edu.gemini.aspen.gmp.epics.simulator;

import com.google.common.collect.Lists;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Simulator is in charge of starting new jobs to simulate EPICS
 * channels. A {@link edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel}
 * contains all the information needed by this class to execute the simulation.
 */
public class Simulator {
    /**
     * The executor service provides a separate thread for the Updater thread
     * to run
     */
    private final ScheduledExecutorService _executorService =
            Executors.newScheduledThreadPool(10);

    private final EpicsRegistrar _registrar;

    private final List<ScheduledFuture<?>> _tasks = Lists.newArrayList();


    public Simulator(EpicsRegistrar registrar) {
        _registrar = registrar;
    }


    public void startSimulation(SimulatedEpicsChannel channel) {
        ScheduledFuture<?> scheduledFuture = _executorService.scheduleAtFixedRate(
                new ChannelSimulator(channel, _registrar), 0, channel.getUpdateRate(), TimeUnit.MILLISECONDS);
        _tasks.add(scheduledFuture);
    }

    public void stopSimulation() {
        for (ScheduledFuture<?> f : _tasks) {
            f.cancel(true);
        }
    }

}
