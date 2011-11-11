package edu.gemini.epics.impl;

import edu.gemini.epics.*;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;

/**
 * Class NewEpicsWriterImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
@Component
@Instantiate
@Provides(specifications = NewEpicsWriter.class)
public class NewEpicsWriterImpl extends EpicsChannelFactory implements NewEpicsWriter {

    public NewEpicsWriterImpl(@Requires JCAContextController epicsService) {
        super(epicsService);
    }

    @Override
    public ReadWriteClientEpicsChannel<Double> getDoubleChannel(String channelName) {
        return _getDoubleChannel(channelName);
    }

    @Override
    public ReadWriteClientEpicsChannel<Integer> getIntegerChannel(String channelName) {
        return _getIntegerChannel(channelName);
    }

    @Override
    public ReadWriteClientEpicsChannel<Float> getFloatChannel(String channelName) {
        return _getFloatChannel(channelName);

    }

    @Override
    public ReadWriteClientEpicsChannel<String> getStringChannel(String channelName) {
        return _getStringChannel(channelName);

    }

    @Override
    public ReadWriteClientEpicsChannel<?> getChannelAsync(String channelName) {
        return _getChannelAsync(channelName);

    }

    @Override
    public void destroyChannel(ReadOnlyClientEpicsChannel<?> channel) throws CAException {
        _destroyChannel(channel);
    }

}
