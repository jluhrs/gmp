package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.Date;

/**
 * Basic parser to construct Status Items whose only difference
 * is in the type of value they contain.
 */
public abstract class SimpleStatusParser<T> extends StatusParserBase<T> {
    StatusItem<T> buildStatusItem(String name, T value, Date timestamp, BytesMessage bm) {
        return new BasicStatus<T>(name, value, timestamp);
    }

    abstract public T getValue(BytesMessage bm) throws JMSException;
}
