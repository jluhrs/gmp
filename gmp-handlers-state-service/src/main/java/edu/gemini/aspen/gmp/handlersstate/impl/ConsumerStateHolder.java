package edu.gemini.aspen.gmp.handlersstate.impl;

import java.util.List;

/**
 * Stores a set of JMS Consumers reflecting the current state of the ActiveMQ Broker
 * 
 */
public interface ConsumerStateHolder {
    List<MessageSubscriber> listSubscribers();
}
