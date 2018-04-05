package edu.gemini.aspen.gmp.status.simulator.simulators;

import com.google.common.collect.Iterators;

import java.util.Iterator;
import java.util.List;

/**
 * Creates a status with a fixed double value
 */
public class StringEnumerationStatusSimulator extends BaseStatusSimulator<String> {
    private final Iterator<String> value;

    StringEnumerationStatusSimulator(String name, long updateRate, List<String> values) {
        super(name, updateRate);
        this.value = Iterators.cycle(values);
    }
    @Override
    String generateValue() {
        return value.next();
    }
}
