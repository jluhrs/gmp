package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import gov.aps.jca.dbr.*;

import javax.jms.Session;
import javax.jms.Message;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class creates JMS messages that contains the EPICS channel update
 * information.
 * <br>
 * The messages constructed by this factory will be de-serialized by the
 * client code that is interested on receiving EPICS status updates.
 */
public class EpicsJmsFactory {


    private static final Logger LOG = Logger.getLogger(EpicsJmsFactory.class.getName());

    /**
     * Create a JMS Message for the specified session, with the information
     * contained in the EPICS update object.
     *
     * @param session JMS Session to be used to construct the message
     * @param update  the EPICS data to be used to construct the message
     * @return a new JMS message containing an EPICS status update ready to
     *         be sent via JMS
     * @throws JMSException in case there is a problem encoding the EPICS
     *                      update using JMS
     */
    public static Message createMessage(Session session, EpicsUpdate<?> update) throws JMSException {

        BytesMessage bm = session.createBytesMessage();
        //Code the data into the message
        List<?> data = update.getChannelData();
        DBRType type = getDataType(data);

        if (type == null) {
            LOG.warning("Invalid type contained in epics update data for channel: "
                    + update.getChannelName() + " data: " + data);
            return null; // not a valid type.
        }

        //store the data type, so it can be reconstructed by readers
        bm.writeByte((byte) type.getValue());

        //then store the name of the status item
        bm.writeUTF(update.getChannelName());

        if (type.isDOUBLE()) {
            List<Double> values = (List<Double>) data;
            bm.writeInt(values.size());
            for (double value : values) {
                bm.writeDouble(value);
            }
        } else if (type.isFLOAT()) {
            List<Float> values = (List<Float>) data;
            bm.writeInt(values.size());
            for (float value : values) {
                bm.writeFloat(value);
            }
        } else if (type.isINT()) {
            List<Integer> values = (List<Integer>) data;
            bm.writeInt(values.size());
            for (int value : values) {
                bm.writeInt(value);
            }
        } else if (type.isSHORT()) {
            List<Short> values = (List<Short>) data;
            bm.writeInt(values.size());
            for (short value : values) {
                bm.writeShort(value);
            }
        } else if (type.isSTRING()) {
            List<String> values = (List<String>) data;
            bm.writeInt(values.size());
            for (String value : values) {
                bm.writeUTF(value);
            }
        } else if (type.isBYTE()) {
            List<Byte> values = (List<Byte>) data;
            bm.writeInt(values.size());
            for (byte value : values) {
                bm.writeByte(value);
            }
        } else {
            LOG.warning("Invalid type for data " + data);
            return null;
        }

        return bm;
    }

    /**
     * Auxiliary method to get the data type of the data object
     *
     * @param data the data object of an epics update
     * @return the data type contained in the data object or <code>null</code>
     *         if the data type is not supported.
     */
    private static DBRType getDataType(List<?> data) {

        if (!data.isEmpty() && data.get(0) instanceof Short) return DBR_Short.TYPE;

        if (!data.isEmpty() && data.get(0) instanceof Integer) return DBR_Int.TYPE;

        if (!data.isEmpty() && data.get(0) instanceof Double) return DBR_Double.TYPE;

        if (!data.isEmpty() && data.get(0) instanceof Float) return DBR_Float.TYPE;

        if (!data.isEmpty() && data.get(0) instanceof String) return DBR_String.TYPE;

        if (!data.isEmpty() && data.get(0) instanceof Byte) return DBR_Byte.TYPE;

        LOG.warning("Unknown data type in data: " + data);
        return null;

    }
}
