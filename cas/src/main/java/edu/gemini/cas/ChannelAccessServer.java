package edu.gemini.cas;

import edu.gemini.epics.api.ReadOnlyChannel;

/**
 * Interface ChannelAccessServer
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 3, 2010
 */
public interface ChannelAccessServer extends ChannelFactory {
    void destroyChannel(ReadOnlyChannel<?> channel);
}
