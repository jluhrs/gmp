package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;

/**
 * The Simulator is in charge of starting new jobs to simulate EPICS
 * channels. A {@link edu.gemini.aspen.gmp.epics.simulator.SimulatedEpicsChannel}
 * contains all the information needed by this class to execute the simulation.
 */
public class Simulator {


       /**
     * The executor service provides a separate thread for the Updater thread
     * to run
     */
    private final ExecutorService _executorService =
            Executors.newFixedThreadPool(10);

    private EpicsRegistrar _registrar;


    private List<Future> _tasks = new ArrayList<Future>();


    public Simulator(EpicsRegistrar registrar) {
        _registrar = registrar;
    }


    public void startSimulation(SimulatedEpicsChannel channel) {
        _tasks.add(_executorService.submit(
                new ChannelSimulator(channel, _registrar))
        );
    }

    public void stopSimulation() {
        for (Future f: _tasks) {
            f.cancel(true);
        }
    }

}
