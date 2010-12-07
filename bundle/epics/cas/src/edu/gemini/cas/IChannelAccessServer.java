package edu.gemini.cas;

/**
 * Interface IChannelAccessServer
 *
 * @author Nicolas A. Barriga
 *         Date: Dec 3, 2010
 */
public interface IChannelAccessServer extends IChannelFactory{
    void destroyChannel(IChannel channel);
}
