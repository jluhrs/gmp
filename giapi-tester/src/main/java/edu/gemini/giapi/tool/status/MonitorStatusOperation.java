package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter;
import edu.gemini.giapi.tool.arguments.*;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Monitor a status item.
 */
public class MonitorStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(MonitorStatusOperation.class.getName());

    private String _statusName;

    private String _host = "localhost";

    private long _timeout = -1;

    private boolean _showMillis = false;

    private String _expectedValue;

    private class StatusMonitor implements StatusHandler {
        private StatusItem lastItem;
        private String expectedValue;

        StatusMonitor(String expectedValue) {
            this.expectedValue = expectedValue;
        }

        @Override
        public String getName() {
            return "Status Monitor";
        }

        @Override
        public <T> void update(StatusItem<T> item) {
            if (!_showMillis) {
                System.out.println("Status value: " + item);
            } else {
                System.out.println("Timestamp: " + item.getTimestamp().getTime() + ", Status value: " + item);
            }
            lastItem = item;
            // If value is found exit immediately
            if (matchesExpectedValue(expectedValue)) {
                System.out.println("Expected value matched expected=" + expectedValue);
                System.exit(0);
            }
        }

        private boolean matchesExpectedValue(String expectedValue) {
            return (expectedValue != null && lastItem != null && lastItem.getValue() != null) && lastItem.getValue().toString().equals(expectedValue);
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
        if (arg instanceof ShowMillisecondsArgument) {
            _showMillis = ((ShowMillisecondsArgument) arg).getExpectedValue();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public int execute() throws Exception {

        // Totally synchronous method to listen for status updates
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter("GIAPI Tester");

        StatusMonitor monitor = new StatusMonitor(_expectedValue);

        StatusHandlerAggregate aggregate = new StatusHandlerAggregate();
        aggregate.bindStatusHandler(monitor);

        StatusService service = new StatusService(aggregate, "Status Monitor Service Client", _statusName);

        try {

            // Read the initial value
            getter.startJms(provider);

            if (_statusName.equals(">")) {
                Collection<StatusItem> items = getter.getAllStatusItems();
                for (StatusItem item : items) {
                    monitor.update(item);
                }
            } else {
                StatusItem item = getter.getStatusItem(_statusName);
                if (item != null) {
                    monitor.update(item);
                }
            }

            getter.stopJms();

            // Start listening
            service.startJms(provider);

            if (_timeout > 0) {//wait the timeout
                ScheduledFuture<Void> timeoutFuture = startTimeoutThread();
                waitForTimeout(timeoutFuture);
            } else if (_timeout == 0) {//exit now
                //do nothing
            } else {//wait forever
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (TimeoutException e) {
            // Ignore. just exit
        } catch (JMSException e) {
            LOG.log(Level.WARNING,"Problem on GIAPI tester",e);
        }
        service.stopJms();

        return 0;
    }

    private void waitForTimeout(ScheduledFuture<Void> futureValue) throws InterruptedException, ExecutionException, TimeoutException {
        if (_timeout > 0) {
            futureValue.get(_timeout, TimeUnit.MILLISECONDS);
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
