package edu.gemini.aspen.gmp.status.simulator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class StatusSimulator creates loops to simulate the value of status items
 */
public class StatusSimulator implements JmsArtifact {
    private final List<StatusSetter> statusSetters;
    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(10);

    public StatusSimulator(SimulatorConfiguration simulatorConfiguration) {
        List<StatusType> statuses = simulatorConfiguration.getStatuses();
        List<StatusSetter> setters = Lists.newArrayList();
        for (StatusType s:statuses) {
            setters.add(new StatusSetter("StatusSimulator-" + s.getName(), s.getName()));
        }
        statusSetters = ImmutableList.copyOf(setters);
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        for (StatusSetter s:statusSetters) {
            s.startJms(provider);
        }
    }

    @Override
    public void stopJms() {
        for (StatusSetter s:statusSetters) {
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
