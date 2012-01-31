package edu.gemini.aspen.gmp.status.simulator.simulators;

import edu.gemini.aspen.gmp.status.simulator.SimulatedStatus;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;

import java.math.BigDecimal;

/**
 * Factory class for status simulator objects
 */
public class StatusSimulatorFactoryBuilder {
    public static StatusSimulatorFactory buildSimulatorFactory(String type, String mode) {
        StatusSimulatorFactory statusSimulatorFactory;
        if (mode.equals("random")) {
            if (type.equals("double")) {
                statusSimulatorFactory = new DoubleRandomStatusSimulatorFactory();
            } else if (type.equals("int")) {
                statusSimulatorFactory = new IntRandomStatusSimulatorFactory();
            } else {
                statusSimulatorFactory = new NullStatusSimulatorFactory();
            }
        } else if (mode.equals("asymptotic-with-noise")) {
            if (type.equals("double")) {
                statusSimulatorFactory = new AsymptoticWithNoiseStatusSimulatorFactory();
            } else {
                // TODO, replace for other types
                statusSimulatorFactory = new NullStatusSimulatorFactory();
            }
        } else if (mode.equals("enumeration")) {
            if (type.equals("string")) {
                statusSimulatorFactory = new StringEnumerationStatusSimulatorFactory();
            } else {
                // TODO, replace for other types
                statusSimulatorFactory = new NullStatusSimulatorFactory();
            }
        } else {
            statusSimulatorFactory = new DoubleFixedStatusSimulatorFactory();
        }
        return statusSimulatorFactory;
    }

    private static class DoubleRandomStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            BigDecimal min = s.getParameters().getMin();
            BigDecimal max = s.getParameters().getMax();
            return new DoubleRandomStatusSimulator(s.getName(),
                    s.getUpdateRate().intValue(),
                    min != null?min.doubleValue():0.0,
                    max != null?max.doubleValue():1.0);
        }
    }

    private static class IntRandomStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            BigDecimal min = s.getParameters().getMin();
            BigDecimal max = s.getParameters().getMax();
            return new DoubleRandomStatusSimulator(s.getName(),
                    s.getUpdateRate().intValue(),
                    min != null?min.doubleValue():0.0,
                    max != null?max.doubleValue():1.0);
        }
    }

    private static class AsymptoticWithNoiseStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            BigDecimal min = s.getParameters().getMin();
            BigDecimal max = s.getParameters().getMax();
            return new AsymptoticWithNoiseStatusSimulatorSimulator(s.getName(),
                    s.getUpdateRate().intValue(),
                    min != null ? min.doubleValue() : 0.0,
                    max != null ? max.doubleValue() : 1.0,
                    1000, 1.0);
        }
    }

    private static class NullStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            return new NullSimulatedStatus(s.getName());
        }
    }

    private static class DoubleFixedStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            return new DoubleFixedStatusSimulator(s.getName(), s.getUpdateRate().intValue(),  0.0);
        }
    }

    private static class StringEnumerationStatusSimulatorFactory implements StatusSimulatorFactory {

        @Override
        public SimulatedStatus buildStatusSimulator(StatusType s) {
            return new StringEnumerationStatusSimulator(s.getName(), s.getUpdateRate().intValue(),  s.getEnumeration().getValue());
        }
    }
}
