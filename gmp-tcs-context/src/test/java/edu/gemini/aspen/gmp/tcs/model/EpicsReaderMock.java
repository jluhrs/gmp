package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsReader;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_String;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;

/**
 * A mockup Epics Reader for testing
 */
public class EpicsReaderMock implements EpicsReader {
    class ReadOnlyClientEpicsChannelMock implements ReadOnlyClientEpicsChannel {
        private String name;
        private Object value;

        public ReadOnlyClientEpicsChannelMock(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void destroy() throws CAException {
            throw new NotImplementedException();
        }

        @Override
        public DBR getDBR() throws CAException, TimeoutException {
            if (value == null) {
                return null;
            } else if (value instanceof String) {
                return new DBR_String((String) value);
            } else {
                return new DBR_Double((double[]) value);
            }
        }

        @Override
        public List getAll() throws CAException, TimeoutException {
            throw new NotImplementedException();
        }

        @Override
        public Object getFirst() throws CAException, TimeoutException {
            throw new NotImplementedException();
        }

        @Override
        public String getName() {
            return name;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void registerListener(ChannelListener channelListener) throws CAException {
            throw new NotImplementedException();
        }

        @Override
        public void unRegisterListener(ChannelListener channelListener) throws CAException {
            throw new NotImplementedException();
        }

        @Override
        public boolean isValid() {
            throw new NotImplementedException();
        }

        @Override
        public DBRType getType() {
            throw new NotImplementedException();
        }
    }

    private ReadOnlyClientEpicsChannel _channel;
    private Object _value;

    @Override
    public ReadOnlyClientEpicsChannel<Double> getDoubleChannel(String channelName) {
        this._channel = new ReadOnlyClientEpicsChannelMock(channelName, _value);
        return _channel;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ReadOnlyClientEpicsChannel<Integer> getIntegerChannel(String channelName) {
        throw new NotImplementedException();
    }

    @Override
    public ReadOnlyClientEpicsChannel<Float> getFloatChannel(String channelName) {
        throw new NotImplementedException();
    }

    @Override
    public ReadOnlyClientEpicsChannel<String> getStringChannel(String channelName) {
        throw new NotImplementedException();
    }

    @Override
    public ReadOnlyClientEpicsChannel<?> getChannelAsync(String channel) throws EpicsException {
        throw new NotImplementedException();
    }

    @Override
    public void destroyChannel(ReadOnlyClientEpicsChannel<?> channel) throws CAException {
        channel.destroy();
    }


    public EpicsReaderMock(String name, Object _value) {
        this._channel = new ReadOnlyClientEpicsChannelMock(name, _value);
        this._value = _value;
    }

    public String getBoundChannel() {
        return _channel.getName();
    }

    @Override
    public String toString() {
        return "EpicsReader{" +
                "channel='" + _channel + '\'' +
                '}';
    }
}
