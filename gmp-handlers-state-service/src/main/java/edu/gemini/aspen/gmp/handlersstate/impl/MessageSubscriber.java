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
        if (clientId == null || destinationName == null) {
            throw new IllegalArgumentException("Cannot accept null values clientId: " + clientId + ", destinationName:" + destinationName);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MessageSubscriber that = (MessageSubscriber) o;

        if (!clientId.equals(that.clientId)) {
            return false;
        }
        if (!destinationName.equals(that.destinationName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = clientId.hashCode();
        result = 31 * result + destinationName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MessageSubscriber{" +
                "clientId='" + clientId + '\'' +
                ", destinationName='" + destinationName + '\'' +
                '}';
    }
}
