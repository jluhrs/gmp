package edu.gemini.aspen.giapi.status.impl;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.HealthStatusItem;
import edu.gemini.aspen.giapi.status.StatusVisitor;

import java.util.Date;

/**
 * A Health Status Item
 */
public class HealthStatus extends BasicStatus<Health> implements HealthStatusItem {

    public Health getHealth() {
        return getValue();
    }

    public HealthStatus(String name, Health status) {
        super(name, status);
    }

    public HealthStatus(String name, Health status, Date timestamp) {
        super(name, status, timestamp);
    }

    @Override
    public String toString() {
        return "HealthStatusItem{" +
                "name='" + getName() + '\'' +
                ", value=" + getValue() +
                ", timestamp=" + getTimestamp() +
                '}';
    }

    @Override
    public void accept(StatusVisitor visitor) throws Exception {
        if (visitor != null) {
            visitor.visitHealthItem(this);
        }
    }
}
