package edu.gemini.aspen.giapi.util.jms.test;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Enumeration;
import java.util.Map;

/**
 * Mock of a map message that is useful to test what is actually intended to
 * send over a JMS message
 *
 * The mock only stores the values without caring about types so this will fail:
 *
 * setString("Value", "a");
 * getInt("Value");
 */
public class MapMessageMock implements MapMessage {
    // We'll use one single map for the whole body
    private Map<String, Object> body = Maps.newHashMap();
    // We'll use one single map for all the properties
    private Map<String, Object> properties = Maps.newHashMap();
    private String messageID;
    private long timestamp;
    private String correlationID;
    private Destination replyTo;
    private Destination jmsDestination;
    private int deliveryMode;
    private boolean jmsRedelivered;
    private String jmsType;
    private long jmsExpiration;
    private int priority;

    @Override
    public boolean getBoolean(String s) throws JMSException {
        return (Boolean)body.get(s);
    }

    @Override
    public byte getByte(String s) throws JMSException {
        return (Byte)body.get(s);
    }

    @Override
    public short getShort(String s) throws JMSException {
        return (Short)body.get(s);
    }

    @Override
    public char getChar(String s) throws JMSException {
        return (Character)body.get(s);
    }

    @Override
    public int getInt(String s) throws JMSException {
        return (Integer)body.get(s);
    }

    @Override
    public long getLong(String s) throws JMSException {
        return (Long)body.get(s);
    }

    @Override
    public float getFloat(String s) throws JMSException {
        return (Float)body.get(s);
    }

    @Override
    public double getDouble(String s) throws JMSException {
        return (Double)body.get(s);
    }

    @Override
    public String getString(String s) throws JMSException {
        return (String)body.get(s);
    }

    @Override
    public byte[] getBytes(String s) throws JMSException {
        return (byte[])body.get(s);
    }

    @Override
    public Object getObject(String s) throws JMSException {
        return body.get(s);
    }

    @Override
    public Enumeration getMapNames() throws JMSException {
        return Iterators.asEnumeration(body.keySet().iterator());
    }

    @Override
    public void setBoolean(String s, boolean b) throws JMSException {
        body.put(s, b);
    }

    @Override
    public void setByte(String s, byte b) throws JMSException {
        body.put(s, b);
    }

    @Override
    public void setShort(String s, short i) throws JMSException {
        body.put(s, i);
    }

    @Override
    public void setChar(String s, char c) throws JMSException {
        body.put(s, c);
    }

    @Override
    public void setInt(String s, int i) throws JMSException {
        body.put(s, i);
    }

    @Override
    public void setLong(String s, long l) throws JMSException {
        body.put(s, l);
    }

    @Override
    public void setFloat(String s, float v) throws JMSException {
        body.put(s, v);
    }

    @Override
    public void setDouble(String s, double v) throws JMSException {
        body.put(s, v);
    }

    @Override
    public void setString(String s, String s1) throws JMSException {
        body.put(s, s1);
    }

    @Override
    public void setBytes(String s, byte[] bytes) throws JMSException {
        body.put(s, bytes);
    }

    @Override
    public void setBytes(String s, byte[] bytes, int i, int i1) throws JMSException {
        throw new UnsupportedOperationException("Don't know how to set partial byte arrays");
    }

    @Override
    public void setObject(String s, Object o) throws JMSException {
        body.put(s, o);
    }

    @Override
    public boolean itemExists(String s) throws JMSException {
        return body.containsKey(s);
    }

    @Override
    public String getJMSMessageID() throws JMSException {
        return messageID;
    }

    @Override
    public void setJMSMessageID(String s) throws JMSException {
        this.messageID = s;
    }

    @Override
    public long getJMSTimestamp() throws JMSException {
        return timestamp;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setJMSTimestamp(long l) throws JMSException {
        this.timestamp = l;
    }

    @Override
    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return correlationID.getBytes();
    }

    @Override
    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {
        this.correlationID = new String(bytes);
    }

    @Override
    public void setJMSCorrelationID(String s) throws JMSException {
        this.correlationID = s;
    }

    @Override
    public String getJMSCorrelationID() throws JMSException {
        return correlationID;
    }

    @Override
    public Destination getJMSReplyTo() throws JMSException {
        return replyTo;
    }

    @Override
    public void setJMSReplyTo(Destination destination) throws JMSException {
        this.replyTo = destination;
    }

    @Override
    public Destination getJMSDestination() throws JMSException {
        return jmsDestination;
    }

    @Override
    public void setJMSDestination(Destination destination) throws JMSException {
        this.jmsDestination = destination;
    }

    @Override
    public int getJMSDeliveryMode() throws JMSException {
        return deliveryMode;
    }

    @Override
    public void setJMSDeliveryMode(int i) throws JMSException {
        this.deliveryMode = i;
    }

    @Override
    public boolean getJMSRedelivered() throws JMSException {
        return jmsRedelivered;
    }

    @Override
    public void setJMSRedelivered(boolean b) throws JMSException {
        this.jmsRedelivered = b;
    }

    @Override
    public String getJMSType() throws JMSException {
        return jmsType;
    }

    @Override
    public void setJMSType(String s) throws JMSException {
        this.jmsType = s;
    }

    @Override
    public long getJMSExpiration() throws JMSException {
        return jmsExpiration;
    }

    @Override
    public void setJMSExpiration(long l) throws JMSException {
        this.jmsExpiration = l;
    }

    @Override
    public int getJMSPriority() throws JMSException {
        return priority;
    }

    @Override
    public void setJMSPriority(int i) throws JMSException {
        this.priority = i;
    }

    @Override
    public void clearProperties() throws JMSException {
        properties.clear();
    }

    @Override
    public boolean propertyExists(String s) throws JMSException {
        return properties.containsKey(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBooleanProperty(String s) throws JMSException {
        return (Boolean)properties.get(s);
    }

    @Override
    public byte getByteProperty(String s) throws JMSException {
        return (Byte)properties.get(s);
    }

    @Override
    public short getShortProperty(String s) throws JMSException {
        return (Short)properties.get(s);
    }

    @Override
    public int getIntProperty(String s) throws JMSException {
        return (Integer)properties.get(s);
    }

    @Override
    public long getLongProperty(String s) throws JMSException {
        return (Long)properties.get(s);
    }

    @Override
    public float getFloatProperty(String s) throws JMSException {
        return (Float)properties.get(s);
    }

    @Override
    public double getDoubleProperty(String s) throws JMSException {
        return (Double)properties.get(s);
    }

    @Override
    public String getStringProperty(String s) throws JMSException {
        return (String)properties.get(s);
    }

    @Override
    public Object getObjectProperty(String s) throws JMSException {
        return properties.get(s);
    }

    @Override
    public Enumeration getPropertyNames() throws JMSException {
        return Iterators.asEnumeration(properties.keySet().iterator());
    }

    @Override
    public void setBooleanProperty(String s, boolean b) throws JMSException {
        properties.put(s, b);
    }

    @Override
    public void setByteProperty(String s, byte b) throws JMSException {
        properties.put(s, b);
    }

    @Override
    public void setShortProperty(String s, short i) throws JMSException {
        properties.put(s, i);
    }

    @Override
    public void setIntProperty(String s, int i) throws JMSException {
        properties.put(s, i);
    }

    @Override
    public void setLongProperty(String s, long l) throws JMSException {
        properties.put(s, l);
    }

    @Override
    public void setFloatProperty(String s, float v) throws JMSException {
        properties.put(s, v);
    }

    @Override
    public void setDoubleProperty(String s, double v) throws JMSException {
        properties.put(s, v);
    }

    @Override
    public void setStringProperty(String s, String s1) throws JMSException {
        properties.put(s, s1);
    }

    @Override
    public void setObjectProperty(String s, Object o) throws JMSException {
        properties.put(s, o);
    }

    @Override
    public void acknowledge() throws JMSException {
    }

    @Override
    public void clearBody() throws JMSException {
        body.clear();
    }
}
