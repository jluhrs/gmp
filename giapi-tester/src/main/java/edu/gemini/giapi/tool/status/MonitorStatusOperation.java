package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregateImpl;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.giapi.tool.arguments.ExpectedValueArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.MonitorStatusArgument;
import edu.gemini.giapi.tool.arguments.TimeoutArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Monitor a status item.
 */
public class MonitorStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(MonitorStatusOperation.class.getName());

    private String _statusName;

    private String _host = "localhost";

    private long _timeout;

    private String _expectedValue;

    private class StatusMonitor implements StatusHandler {
        private StatusItem lastItem;
        private String expectedValue;

        StatusMonitor(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        public String getName() {
            return "Status Monitor";
        }

        public void update(StatusItem item) {
            System.out.println("Status value: " + item);
            lastItem = item;
            // If value is found exit immediately
            if (matchesExpectedValue(expectedValue)) {
                System.out.println("Expected value matched expected=" + expectedValue);
                System.exit(0);
            }
        }

        private boolean matchesExpectedValue(String expectedValue) {
            return expectedValue != null && lastItem != null && lastItem.getValue() != null ? lastItem.getValue().toString().equals(expectedValue) : false;
        }
    }

    public void setArgument(Argument arg) {
        if (arg instanceof MonitorStatusArgument) {
            _statusName = ((MonitorStatusArgument) arg).getStatusName();
        }
        if (arg instanceof HostArgument) {
            _host = ((HostArgument) arg).getHost();
        }
        if (arg instanceof TimeoutArgument) {
            _timeout = ((TimeoutArgument) arg).getTimeout();
        }
        if (arg instanceof ExpectedValueArgument) {
            _expectedValue = ((ExpectedValueArgument) arg).getExpectedValue();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    public void execute() throws Exception {

        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter();

        StatusMonitor monitor = new StatusMonitor(_expectedValue);

        StatusHandlerAggregate aggregate = new StatusHandlerAggregateImpl();
        aggregate.bindStatusHandler(monitor);

        StatusService service = new StatusService(aggregate, "Status Monitor Service Client", _statusName, provider);

        try {

            getter.startJms(provider);

            StatusItem item = getter.getStatusItem(_statusName);

            monitor.update(item);

            getter.stopJms();

            ScheduledFuture<Void> timeoutFuture = startTimeoutThread();

            service.initialize();

            waitForTimeout(service, timeoutFuture);

        } catch (JMSException e) {
            LOG.warning("Problem on GIAPI tester: " + e.getMessage());
        }

    }

    private void waitForTimeout(StatusService service, ScheduledFuture<Void> futureValue) throws InterruptedException, ExecutionException, TimeoutException {
        if (_timeout > 0) {
            futureValue.get(_timeout, TimeUnit.MILLISECONDS);
            service.stopJms();
            if (_expectedValue != null) {
                System.out.println("After " + _timeout + " ms monitor has timed out not reaching the value expected=" + _expectedValue);
                System.exit(1);
            } else {
                System.exit(0);
            }
        }
    }

    private ScheduledFuture<Void> startTimeoutThread() {
        ScheduledFuture<Void> futureValue = null;
        if (_timeout > 0) {
            // Set a watching thread
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            futureValue = scheduler.schedule(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return null;
                }
            }, _timeout, TimeUnit.MILLISECONDS);
        }
        return futureValue;
    }
}
