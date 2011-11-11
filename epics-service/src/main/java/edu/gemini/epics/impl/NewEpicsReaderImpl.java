package edu.gemini.epics.impl;

import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.JCAContextController;
import edu.gemini.epics.NewEpicsReader;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.*;

/**
 * Class NewEpicsReaderImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 11/7/11
 */
@Component
@Instantiate
@Provides(specifications = NewEpicsReader.class)
public class NewEpicsReaderImpl extends EpicsChannelFactory implements NewEpicsReader {

    public NewEpicsReaderImpl(@Requires JCAContextController epicsService) {
        super(epicsService);
    }

    @Validate
    public void validate() {

    }

    @Invalidate
    public void invalidate() {

    }

    @Override
    public ReadOnlyClientEpicsChannel<Double> getDoubleChannel(String channelName) {
        return _getDoubleChannel(channelName);
    }

    @Override
    public ReadOnlyClientEpicsChannel<Integer> getIntegerChannel(String channelName) {
        return _getIntegerChannel(channelName);
    }

    @Override
    public ReadOnlyClientEpicsChannel<Float> getFloatChannel(String channelName) {
        return _getFloatChannel(channelName);

    }

    @Override
    public ReadOnlyClientEpicsChannel<String> getStringChannel(String channelName) {
        return _getStringChannel(channelName);

    }

    @Override
    public ReadOnlyClientEpicsChannel<?> getChannelAsync(String channelName) {
        return _getChannelAsync(channelName);

    }

    @Override
    public void destroyChannel(ReadOnlyClientEpicsChannel<?> channel) throws CAException {
        _destroyChannel(channel);
    }

}
