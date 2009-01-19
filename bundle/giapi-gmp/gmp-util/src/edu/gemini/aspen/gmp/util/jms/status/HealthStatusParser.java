package edu.gemini.aspen.gmp.util.jms.status;

import edu.gemini.aspen.gmp.status.api.Health;
import edu.gemini.aspen.gmp.status.api.StatusItem;
import edu.gemini.aspen.gmp.status.impl.HealthStatus;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * A parser for health status items
 */
public class HealthStatusParser extends StatusParserBase<Health> {
    Health getValue(BytesMessage bm) throws JMSException {

        //health is coded as a byte, and there are only 3 accepted values
        int healthCode = bm.readInt();
        if (healthCode < 0 || healthCode >= 3)
            throw new IllegalArgumentException("No Health value associated to code " + healthCode);

        switch (healthCode) {
            case 0: return Health.GOOD;
            case 1: return Health.WARNING;
            case 2: return Health.BAD;
        }

        //if we are here, then return the default health
        return Health.DEFAULT;

    }

    StatusItem buildStatusItem(String name, Health value, BytesMessage bm) throws JMSException {
        return new HealthStatus(name, value);
    }
}
