package edu.gemini.aspen.gmp.status.impl;

import edu.gemini.aspen.gmp.status.api.Health;
import edu.gemini.aspen.gmp.status.api.HealthStatusItem;
import edu.gemini.aspen.gmp.status.api.StatusVisitor;

/**
 * A Health Status Item
 */
public class HealthStatus extends BasicStatus<Health> implements HealthStatusItem {

    public Health getHealth() {
        return getValue();
    }

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

    @Override
    public void accept(StatusVisitor visitor) throws Exception {
        if (visitor != null) {
            visitor.visitHealthItem(this);
        }
    }
}
