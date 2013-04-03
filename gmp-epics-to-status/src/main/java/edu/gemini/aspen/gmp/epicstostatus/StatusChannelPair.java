package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;

class StatusChannelPair {
    public final StatusSetter statusSetter;
    public final ReadOnlyClientEpicsChannel channel;

    StatusChannelPair(StatusSetter statusSetter, ReadOnlyClientEpicsChannel channel) {
        this.statusSetter = statusSetter;
        this.channel = channel;
    }
}
