package edu.gemini.aspen.gmp.status.simulator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import edu.gemini.aspen.gmp.status.simulator.simulators.StatusSimulatorFactory;
import edu.gemini.aspen.gmp.status.simulator.simulators.StatusSimulatorFactoryBuilder;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class StatusSimulator creates loops to simulate the value of status items
 */
@Component
@Provides
public class StatusSimulator implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(StatusSimulator.class.getName());
    private final Map<SimulatedStatus, StatusSetter> statusSetters;
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(10);
    private final List<ScheduledFuture<?>> _tasks = Lists.newArrayList();

    public StatusSimulator(@Property(name = "simulationConfiguration", value = "NOVALID", mandatory = true) String configFile) throws JAXBException, FileNotFoundException {
        LOG.info("Simulating using configuration at " + configFile);
        SimulatorConfiguration simulatorConfiguration =  new SimulatorConfiguration(new FileInputStream(configFile));
        List<StatusType> statuses = simulatorConfiguration.getStatuses();
        Map<SimulatedStatus, StatusSetter> simulatorsMap =  Maps.newHashMap();
        for (StatusType s:statuses) {
            StatusSetter statusSetter = new StatusSetter("StatusSimulator-" + s.getName(), s.getName());
            SimulatedStatus simulatedStatus =  buildSimulatedStatus(s);
            simulatorsMap.put(simulatedStatus, statusSetter);
        }
        statusSetters = ImmutableMap.copyOf(simulatorsMap);
    }

    @Validate
    public void startComponent() throws JMSException {
    }

    private SimulatedStatus buildSimulatedStatus(StatusType s) {
        String type = s.getType();
        String mode = s.getMode();
        StatusSimulatorFactory simulator = buildFactory(type, mode);
        return simulator.buildStatusSimulator(s);
    }

    private StatusSimulatorFactory buildFactory(String type, String mode) {
        return StatusSimulatorFactoryBuilder.buildSimulatorFactory(type, mode);
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        for (StatusSetter s:statusSetters.values()) {
            s.startJms(provider);
        }
        startSimulation();
    }

    @Override
    public void stopJms() {
        stopSimulation();
        for (StatusSetter s:statusSetters.values()) {
            s.stopJms();
        }
    }

    public synchronized void startSimulation() {
        LOG.info("Start status items simulation");
        for (Map.Entry<SimulatedStatus, StatusSetter> s:statusSetters.entrySet()) {
            SimulatedStatus simulatedStatus = s.getKey();
            LOG.info("Simulate status item " + simulatedStatus.getName() + " at " + simulatedStatus.getUpdateRate() + " with " + simulatedStatus.getClass().getSimpleName());
            ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
                    new SimulationTask(simulatedStatus, s.getValue()), 0, simulatedStatus.getUpdateRate(), TimeUnit.MILLISECONDS);
            _tasks.add(scheduledFuture);
        }
    }

    public synchronized void stopSimulation() {
        for (ScheduledFuture<?> f : _tasks) {
            f.cancel(true);
        }
    }

    private class SimulationTask implements Runnable {
        private final SimulatedStatus status;
        private final StatusSetter setter;

        public SimulationTask(SimulatedStatus status, StatusSetter setter) {
            this.status = status;
            this.setter = setter;
        }

        @Override
        public void run() {
            try {
                setter.setStatusItem(status.simulateOnce());
            } catch (JMSException e) {
                LOG.warning("Exception when setting a status item: " + status);
            }
        }
    }
}
