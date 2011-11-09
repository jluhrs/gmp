package edu.gemini.epics;

import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ReadOnlyChannel;

/**
 * Interface NewEpicsWriter
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
public interface NewEpicsWriter extends NewEpicsReader {
    @Override
    ReadWriteClientEpicsChannel<Double> getDoubleChannel(String channelName);

    @Override
    ReadWriteClientEpicsChannel<Integer> getIntegerChannel(String channelName);

    @Override
    ReadWriteClientEpicsChannel<Float> getFloatChannel(String channelName);

    @Override
    ReadWriteClientEpicsChannel<String> getStringChannel(String channelName);

    @Override
    ReadWriteClientEpicsChannel<?> getChannelAsync(String channelName);
}
