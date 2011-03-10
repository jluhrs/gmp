package edu.gemini.aspen.heartbeatdistributor;

/**
 * Interface IHeartbeatConsumer
 *
 * @author Nicolas A. Barriga
 *         Date: 3/10/11
 */
public interface IHeartbeatConsumer {
    void beat(long beatNumber);
    String getName();
}
