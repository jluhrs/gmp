package edu.gemini.gmp.top;

/**
 * Class TopImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
public class TopImpl implements Top {

    private final String epicsTop;
    private final String giapiTop;

    public TopImpl(String epicsTop, String giapiTop) {
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
