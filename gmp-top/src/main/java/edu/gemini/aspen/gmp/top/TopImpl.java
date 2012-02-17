package edu.gemini.aspen.gmp.top;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;

/**
 * Class TopImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
@Component
@Provides
public class TopImpl implements Top {

    private final String epicsTop;
    private final String giapiTop;

    public TopImpl(@Property(name = "epicsTop", value = "INVALID", mandatory = true) String epicsTop,
                   @Property(name = "giapiTop", value = "INVALID", mandatory = true) String giapiTop) {
        this.epicsTop = epicsTop + ":";
        this.giapiTop = giapiTop + ":";
    }

    @Override
    public String buildEpicsChannelName(String name) {
        return epicsTop + name;
    }

    @Override
    public String buildStatusItemName(String name) {
        return giapiTop + name;
    }
}
