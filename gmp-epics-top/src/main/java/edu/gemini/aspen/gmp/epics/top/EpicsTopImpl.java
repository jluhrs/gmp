package edu.gemini.aspen.gmp.epics.top;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * Class EpicsTopImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
@Component
@Provides
public class EpicsTopImpl implements EpicsTop {

    private final String epicsTop;

    public EpicsTopImpl(@Property(name = "epicsTop", value = "INVALID", mandatory = true) String epicsTop) {
        this.epicsTop = epicsTop;
    }

    @Override
    public String buildChannelName(String name) {
        return epicsTop + ":" + name;
    }
}
