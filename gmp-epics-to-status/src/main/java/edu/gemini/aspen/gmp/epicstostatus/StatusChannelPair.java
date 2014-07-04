package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;

class StatusChannelPair {
    public final StatusSetterImpl statusSetter;
    public final ReadOnlyClientEpicsChannel channel;

    StatusChannelPair(StatusSetterImpl statusSetter, ReadOnlyClientEpicsChannel channel) {
        this.statusSetter = statusSetter;
        this.channel = channel;
    }
}
