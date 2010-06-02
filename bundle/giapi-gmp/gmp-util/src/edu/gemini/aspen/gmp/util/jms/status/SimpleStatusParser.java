package edu.gemini.aspen.gmp.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * Basic parser to construct Status Items whose only difference
 * is in the type of value they contain. 
 */
public abstract class SimpleStatusParser<T> extends StatusParserBase<T> {
    StatusItem buildStatusItem(String name, T value, BytesMessage bm) {
        return new BasicStatus<T>(name, value);
    }

    abstract public T getValue(BytesMessage bm) throws JMSException;
}
