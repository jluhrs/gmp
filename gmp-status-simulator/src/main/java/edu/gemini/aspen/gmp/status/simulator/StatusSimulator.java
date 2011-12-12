package edu.gemini.aspen.gmp.status.simulator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import edu.gemini.aspen.gmp.status.simulator.simulators.DoubleFixedSimulatedStatus;
import edu.gemini.aspen.gmp.status.simulator.simulators.DoubleRandomSimulatedStatus;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class StatusSimulator creates loops to simulate the value of status items
 */
public class StatusSimulator implements JmsArtifact {
    private final Map<SimulatedStatus, StatusSetter> statusSetters;
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(10);

    public StatusSimulator(SimulatorConfiguration simulatorConfiguration) {
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
                simulator = new DoubleRandomSimulatedStatus(s.getName());
            } else {
                // TODO, replace for other types
                simulator = new DoubleRandomSimulatedStatus(s.getName());
            }
        } else {
            simulator = new DoubleFixedSimulatedStatus(s.getName(), 0.0);
        }
        return simulator;
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        for (StatusSetter s:statusSetters.values()) {
            s.startJms(provider);
        }
    }

    @Override
    public void stopJms() {
        for (StatusSetter s:statusSetters.values()) {
            s.stopJms();
        }
    }

    /*public void startSimulation(SimulatedStatus channel) {
        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(
                new SimulatedStatus(channel, _registrar), 0, channel.getUpdateRate(), TimeUnit.MILLISECONDS);
        _tasks.add(scheduledFuture);
    }

    public void stopSimulation() {
        for (ScheduledFuture<?> f : _tasks) {
            f.cancel(true);
        }
    }*/

}
