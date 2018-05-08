package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.Map;
import java.util.HashMap;

/**
 * Contains definition of different parsers to construct different types
 * of status items
 */
public enum StatusItemParser {

    /**
     * Fist, basic status item parser. Only a value is contained
     */
    INT(0, new SimpleStatusParser<Integer>() {
        public Integer getValue(BytesMessage bm) throws JMSException {
            return bm.readInt();
        }
    }),
    DOUBLE(1, new SimpleStatusParser<Double>() {
        public Double getValue(BytesMessage bm) throws JMSException {
            return bm.readDouble();
        }
    }),
    FLOAT(2, new SimpleStatusParser<Float>() {
        public Float getValue(BytesMessage bm) throws JMSException {
            return bm.readFloat();
        }
    }),
    STRING(3, new SimpleStatusParser<String>() {
        public String getValue(BytesMessage bm) throws JMSException {
            return bm.readUTF();
        }
    }),

    /**
     * Construct Alarm Status Items. In addition to the value, they
     * contain an alarm state
     */
    ALARM_INT(10, new AlarmStatusParser<Integer>() {
        public Integer getValue(BytesMessage bm) throws JMSException {
            return bm.readInt();
        }
    }),
    ALARM_DOUBLE(11, new AlarmStatusParser<Double>() {
        public Double getValue(BytesMessage bm) throws JMSException {
            return bm.readDouble();
        }
    }),
    ALARM_FLOAT(12, new AlarmStatusParser<Float>() {
        public Float getValue(BytesMessage bm) throws JMSException {
            return bm.readFloat();
        }
    }),
    ALARM_STRING(13, new AlarmStatusParser<String>() {
        public String getValue(BytesMessage bm) throws JMSException {
            return bm.readUTF();
        }
    }),


    HEALTH(20, new HealthStatusParser());


    <T> StatusItem<T> parseStatus(BytesMessage bm) throws JMSException {
        return _parser.parse(bm);
    }

    StatusItemParser(int code, StatusParser parser) {
        _code = code;
        _parser = parser;
    }

    private int _code;
    private StatusParser _parser;


    private static Map<Integer, StatusItemParser> _types = new HashMap<>();

    static {
        for (StatusItemParser itemParser : StatusItemParser.values()) {
            _types.put(itemParser._code, itemParser);
        }
    }


    /**
     * Creates a Status Item object from the JMS BytesMessage.
     *
     * @param bm the bytes message JMS message containing information to
     *           deserialize a Status Item object
     * @return a new Status Item object
     * @throws IllegalArgumentException if the information in the byte
     *                                  message contains invalid information to construct a Status Item
     * @throws JMSException             if there is a problem reading information from the
     *                                  BytesMessage
     */
    public static <T> StatusItem<T> parse(BytesMessage bm) throws IllegalArgumentException, JMSException {
        if (bm.getBodyLength() < 1) return null; // no content
        int code = bm.readByte();
        StatusItemParser itemParser = _types.get(code);
        if (itemParser == null) throw new IllegalArgumentException("No Status Type associated to code " + code);
        return itemParser.parseStatus(bm);
    }

}
