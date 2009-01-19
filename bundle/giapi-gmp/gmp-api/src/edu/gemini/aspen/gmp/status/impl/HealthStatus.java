package edu.gemini.aspen.gmp.status.impl;

import edu.gemini.aspen.gmp.status.api.Health;

/**
 * A Health Status Item
 */
public class HealthStatus extends BasicStatus<Health> {

    public HealthStatus(String name,  Health status) {
        super(name, status);
    }

    @Override
    public String toString() {
        return "HealthStatusItem{" +
                "name='" + getName() + '\'' +
                ", value=" + getValue() +
                '}';
    }

}
