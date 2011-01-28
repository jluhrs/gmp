package edu.gemini.aspen.gmp.handlersstate.impl;

/**
 * Simple Holder class that contains a description of a Listener of a given topic/queue
 */
class MessageSubscriber {

    private final String clientId;
    private final String destinationName;

    public MessageSubscriber(String clientId, String destinationName) {
        this.clientId = clientId;
        this.destinationName = destinationName;
    }

    @Override
    public String toString() {
        return "MessageSubscriber{" +
                "clientId='" + clientId + '\'' +
                ", destinationName='" + destinationName + '\'' +
                '}';
    }
}
