package edu.gemini.cas;

/**
 * Interface ChannelAccessServer
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 3, 2010
 */
public interface ChannelAccessServer extends ChannelFactory {
    void destroyChannel(Channel channel);
}
