package edu.gemini.aspen.gmp.epics.simulator;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.simulator.channels.SimulatedEpicsChannel;
import org.apache.felix.ipojo.annotations.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class EpicsSimulatorComponent {
    private static final Logger LOG = Logger.getLogger(EpicsSimulatorComponent.class.getName());
    private final String _simulationConfigFile;

    private final EpicsRegistrar _registrar;

    private Simulator _simulator;
    private SimulatedEpicsConfiguration conf;

    public EpicsSimulatorComponent(@Requires EpicsRegistrar registrar,
            @Property(name = "simulationConfiguration", value = "NOVALID", mandatory = true) String simulationConfigFile) {
        this._registrar = registrar;
        this._simulationConfigFile = simulationConfigFile;
    }

    @Updated
    public void readConfiguration() {
        try {
            conf = new XMLBasedSimulatedEpicsConfiguration(new FileInputStream(_simulationConfigFile));
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Missing configuration file, cannot start", e);
        }
    }

    @Validate
    public void startSimulation() {
        if (isConfigurationAvailable()) {
            LOG.info("GMP Epics Registrar module found. Starting simulation");
            createAndStartSimulation();
        }
    }

    private void createAndStartSimulation() {
        _simulator = new Simulator(_registrar);

        for (SimulatedEpicsChannel channel : conf.getSimulatedChannels()) {
            LOG.info("Starting simulation of channel " + channel.getName());
            _simulator.startSimulation(channel);
        }
    }

    private boolean isConfigurationAvailable() {
        return conf != null;
    }

    @Invalidate
    public void stopSimulation() {
        if (isSimulationRunning()) {
            LOG.info("Stopping simulation");
            _simulator.stopSimulation();
        }
    }

    private boolean isSimulationRunning() {
        return _simulator != null;
    }
}
