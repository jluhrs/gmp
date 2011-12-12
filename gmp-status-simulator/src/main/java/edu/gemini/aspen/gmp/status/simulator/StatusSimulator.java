package edu.gemini.aspen.gmp.status.simulator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import edu.gemini.aspen.gmp.status.simulator.simulators.DoubleFixedSimulatedStatus;
import edu.gemini.aspen.gmp.status.simulator.simulators.DoubleRandomSimulatedStatus;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Class StatusSimulator creates loops to simulate the value of status items
 */
@Component
@Service
public class StatusSimulator implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(StatusSimulator.class.getName());
    private final Map<SimulatedStatus, StatusSetter> statusSetters;
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(10);
    private final List<ScheduledFuture<?>> _tasks = Lists.newArrayList();

    public StatusSimulator(@Property(name = "simulationConfiguration", value = "NOVALID", mandatory = true) String configFile) throws JAXBException {
        SimulatorConfiguration simulatorConfiguration =  new SimulatorConfiguration(this.getClass().getClassLoader().getResourceAsStream(configFile));
        List<StatusType> statuses = simulatorConfiguration.getStatuses();
        Map<SimulatedStatus, StatusSetter> simulatorsMap =  Maps.newHashMap();
        for (StatusType s:statuses) {
            StatusSetter statusSetter = new StatusSetter("StatusSimulator-" + s.getName(), s.getName());
            SimulatedStatus simulatedStatus =  buildSimulatedStatus(s);
            simulatorsMap.put(simulatedStatus, statusSetter);
        }
        statusSetters = ImmutableMap.copyOf(simulatorsMap);
    }

    private SimulatedStatus buildSimulatedStatus(StatusType s) {
        SimulatedStatus simulator;
        String type = s.getType();
        if (type.equals("random")) {
            if (s.getType().equals("double")) {
                simulator = new DoubleRandomSimulatedStatus(s.getName(), s.getUpdateRate());
            } else {
                // TODO, replace for other types
                simulator = new DoubleRandomSimulatedStatus(s.getName(), s.getUpdateRate());
            }
        } else {
            simulator = new DoubleFixedSimulatedStatus(s.getName(), s.getUpdateRate(),  0.0);
        }
        return simulator;
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
        for (Map.Entry<SimulatedStatus, StatusSetter> s:statusSetters.entrySet()) {
            ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
                    new SimulationTask(s.getKey(), s.getValue()), 0, s.getKey().getUpdateRate(), TimeUnit.MILLISECONDS);
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
