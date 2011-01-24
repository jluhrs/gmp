package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
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

        public String getName() {
            return "Status Monitor";
        }

        public void update(StatusItem item) {
            System.out.println("Status value: " + item);
            lastItem = item;
        }

        boolean doesLastStatusMatch(String expectedValue) {
            if (expectedValue == null) {
                return true;
            }
            return lastItem != null?lastItem.getValue().toString().equals(expectedValue):false;
        }
    }

    public void setArgument(Argument arg) {
        if (arg instanceof MonitorStatusArgument) {
            _statusName = ((MonitorStatusArgument)arg).getStatusName();
        } if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        } if (arg instanceof TimeoutArgument) {
            _timeout =  ((TimeoutArgument)arg).getTimeout();
        } if (arg instanceof ExpectedValueArgument) {
            _expectedValue = ((ExpectedValueArgument)arg).getExpectedValue();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    public void execute() throws Exception {

        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter();

        StatusMonitor monitor = new StatusMonitor();

        StatusService service = new StatusService("Status Monitor Service Client", _statusName);
        service.addStatusHandler(monitor);

        try {

            getter.startJms(provider);

            StatusItem item = getter.getStatusItem(_statusName);

            monitor.update(item);

            getter.stopJms();

            ScheduledFuture<Void> timeoutFuture = startTimeoutThread();

            service.startJms(provider);

            waitForTimeout(service, timeoutFuture);

            compareWithExpected(monitor);

        } catch (JMSException e) {
            LOG.warning("Problem on GIAPI tester: " + e.getMessage());
        }

    }

    private void compareWithExpected(StatusMonitor monitor) {
        if (_timeout > 0 && _expectedValue != null) {
            // Could be nice no to exit this way
            System.exit(monitor.doesLastStatusMatch(_expectedValue)?0:1);
        }
    }

    private void waitForTimeout(StatusService service, ScheduledFuture<Void> futureValue) throws InterruptedException, ExecutionException, TimeoutException {
        if (_timeout > 0) {
            futureValue.get(_timeout, TimeUnit.MILLISECONDS);
            service.stopJms();
            System.out.println("After " + _timeout + " ms it timed out");
            if (_expectedValue == null) {
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
