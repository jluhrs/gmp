package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.*;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

/**
 * This visitor is used to code the status items objects into
 * JMS messages. The visitor takes as an argument the JMS message
 * (in the form of a BytesMessage) that will be filled in with
 * the content of the appropriate status item.
 */
public class StatusSerializerVisitor implements StatusVisitor {

    /**
     * Offsets used to serialize the messages and distinguish
     * among the different types.
     */
    enum Offset {
        BASIC((byte) 0),
        ALARM((byte) 10),
        HEALTH((byte) 20);

        private final byte _offset;

        Offset(byte offset) {
            _offset = offset;
        }

        byte getOffset() {
            return _offset;
        }

    }

    /**
     * The message that will be filled in by this visitor
     */
    private BytesMessage _msg;

    /**
     * Constructor. Takes as argument the byte message where
     * the status item will be serialized
     *
     * @param bm JMS Bytes Message to be used to serialize the
     *           status item.
     */
    public StatusSerializerVisitor(BytesMessage bm) {
        _msg = bm;
    }

    /**
     * Serialize a Status Item into the JMS message
     */
    public void visitStatusItem(StatusItem status) throws JMSException {
        _writeHeader(Offset.BASIC, status);
    }

    /**
     * Serialize a Alarm Status Item into the JMS message
     */
    public void visitAlarmItem(AlarmStatusItem status) throws JMSException {
        _writeHeader(Offset.ALARM, status);

        AlarmState state = status.getAlarmState();

        //first we add the severity
        switch (state.getSeverity()) {
            case ALARM_OK:
                _msg.writeByte((byte) 0);
                break;
            case ALARM_WARNING:
                _msg.writeByte((byte) 1);
                break;
            case ALARM_FAILURE:
                _msg.writeByte((byte) 2);
                break;
        }

        //now the cause
        switch (state.getCause()) {
            case ALARM_CAUSE_OK:
                _msg.writeByte((byte) 0);
                break;

            case ALARM_CAUSE_HIHI:
                _msg.writeByte((byte) 1);
                break;

            case ALARM_CAUSE_HI:
                _msg.writeByte((byte) 2);
                break;

            case ALARM_CAUSE_LOLO:
                _msg.writeByte((byte) 3);
                break;

            case ALARM_CAUSE_LO:
                _msg.writeByte((byte) 4);
                break;

            case ALARM_CAUSE_OTHER:
                _msg.writeByte((byte) 5);
                break;
        }

        if (state.getMessage() != null) {
            _msg.writeBoolean(true);
            _msg.writeUTF(state.getMessage());
        } else {
            _msg.writeBoolean(false);
        }


    }

    /**
     * Serialize a Health Status Item into the JMS message
     */
    public void visitHealthItem(HealthStatusItem status) throws JMSException {
        _msg.writeByte(Offset.HEALTH.getOffset());
        _msg.writeUTF(status.getName());
        switch (status.getHealth()) {
            case GOOD:
                _msg.writeInt(0);
                break;
            case WARNING:
                _msg.writeInt(1);
                break;
            case BAD:
                _msg.writeInt(2);
                break;
        }
        _msg.writeLong(status.getTimestamp().getTime());
    }

    /**
     * Auxiliary method to write  common information
     * for all the status items, like the name and their
     * value. The offset is used when coding the
     * message to distinguish the different types of
     * status when reconstructing
     *
     * @param offset Offset to be used to distinguish different
     *               types of status items
     * @param item   the status item to encode
     * @throws javax.jms.JMSException if a problem occurs while
     *                                encoding the status item
     */
    private void _writeHeader(Offset offset, StatusItem item) throws JMSException {

        Object o = item.getValue();

        if (o instanceof Integer) {
            _msg.writeByte(offset.getOffset());
            _msg.writeUTF(item.getName());
            _msg.writeInt((Integer) o);
            _msg.writeLong(item.getTimestamp().getTime());
        } else if (o instanceof Double) {
            _msg.writeByte((byte) (offset.getOffset() + 1));
            _msg.writeUTF(item.getName());
            _msg.writeDouble((Double) o);
            _msg.writeLong(item.getTimestamp().getTime());
        } else if (o instanceof Float) {
            _msg.writeByte((byte) (offset.getOffset() + 2));
            _msg.writeUTF(item.getName());
            _msg.writeFloat((Float) o);
            _msg.writeLong(item.getTimestamp().getTime());
        } else if (o instanceof String) {
            _msg.writeByte((byte) (offset.getOffset() + 3));
            _msg.writeUTF(item.getName());
            _msg.writeUTF((String) o);
            _msg.writeLong(item.getTimestamp().getTime());
        }
    }

}
