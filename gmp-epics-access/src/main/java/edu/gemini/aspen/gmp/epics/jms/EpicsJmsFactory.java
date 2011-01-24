package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;

import javax.jms.Session;
import javax.jms.Message;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * This class creates JMS messages that containsn the EPICS channel update
 * information.
 * <p/>
 * The messages constructed by this factory will be deserialized by the
 * client code that is interested on receiving EPICS status updates.
 *
 */
public class EpicsJmsFactory {


    private static final Logger LOG = Logger.getLogger(EpicsJmsFactory.class.getName());

    /**
     * Data type and byte codes used to encode the EPICS updates into JMS
     * messges
     */
    private enum DataType {

        SHORT((byte) 1),
        INT((byte) 2),
        DOUBLE((byte) 3),
        FLOAT((byte) 4),
        STRING((byte) 5),
        BYTE((byte) 6);

        private byte _code;

        DataType(byte code) {
            _code = code;
        }

        byte getCode() {
            return _code;
        }
    }

    /**
     * Create a JMS Message for the specified session, with the information
     * contained in the EPICS update object.
     * @param session JMS Session to be used to construct the message
     * @param update the EPICS data to be used to construct the message
     * @return a new JMS message containing an EPICS status update ready to
     * be sent via JMS
     * @throws JMSException in case there is a problem encoding the EPICS
     * update using JMS
     */
    public static Message createMessage(Session session, EpicsUpdate update) throws JMSException {

        BytesMessage bm = session.createBytesMessage();
        //Code the data into the message
                Object data = update.getChannelData();
        DataType type = getDataType(data);

        if (type == null) {
            LOG.warning("Invalid type contained in epics update data for channel: "
                    + update.getChannelName() + " data: " + data);
            return null; // not a valid type.
        }

        //store the data type, so it can be reconstructed by readers
        bm.writeByte(type.getCode());

        //then store the name of the status item
        bm.writeUTF(update.getChannelName());


        switch (type) {

            case DOUBLE: {
                double[] values = (double[]) data;
                bm.writeInt(values.length);
                for (double value : values) {
                    bm.writeDouble(value);
                }
            }
            break;

            case FLOAT: {
                float[] values = (float[]) data;
                bm.writeInt(values.length);
                for (float value : values) {
                    bm.writeFloat(value);
                }
            }
            break;

            case INT:{
                int[] values = (int[]) data;
                bm.writeInt(values.length);
                for (int value : values) {
                    bm.writeInt(value);
                }
            }
                break;
            case SHORT:{
                short[] values = (short[]) data;
                bm.writeInt(values.length);
                for (short value : values) {
                    bm.writeShort(value);
                }
            }
                break;
            case STRING:{
                String[] values = (String[]) data;
                bm.writeInt(values.length);
                for (String value : values) {
                    bm.writeUTF(value);
                }
            }
                break;
            case BYTE:{
                byte[] values = (byte[]) data;
                bm.writeInt(values.length);
                for (byte value : values) {
                    bm.writeByte(value);
                }
            }
            break;

            default:
                LOG.warning("Invalid type for data " + data);

        }
        
        return bm;
    }

    /**
     * Auxiliary method to get the data type of the data object
     * @param data the data object of an epics update
     * @return the data type contained in the data object or <code>null</code>
     * if the data type is not supported.
     */
    private static DataType getDataType(Object data) {


        if (data instanceof short[]) return DataType.SHORT;

        if (data instanceof int[]) return DataType.INT;

        if (data instanceof double[]) return DataType.DOUBLE;

        if (data instanceof float[]) return DataType.FLOAT;

        if (data instanceof String[]) return DataType.STRING;

        if (data instanceof byte[]) return DataType.BYTE;

        LOG.info("Unknown data type in data: " + data);
        return null;

    }
}
