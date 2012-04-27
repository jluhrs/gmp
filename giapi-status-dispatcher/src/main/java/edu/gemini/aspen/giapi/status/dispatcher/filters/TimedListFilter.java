package edu.gemini.aspen.giapi.status.dispatcher.filters;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Class TimedListFilter extends a ListFilter, adding a restriction on the frequency of matches.
 *
 * @author Nicolas A. Barriga
 *         Date: 4/26/12
 */
public class TimedListFilter extends ListFilter {
    private final Duration duration;
    private DateTime last;

    /**
     * @param interval Minimum interval between successful matches
     * @param filters
     */
    public TimedListFilter(Duration interval, String... filters) {
        super(filters);
        this.duration = interval;
        last = new DateTime().minus(duration).minus(duration);
    }

    /**
     * @param frequency Maximum frequency of successful matches
     * @param filters
     */
    public TimedListFilter(int frequency, String... filters) {
        super(filters);
        this.duration = new Duration(1000 / frequency);
        last = new DateTime().minus(duration).minus(duration);
    }

    @Override
    synchronized public boolean match(StatusItem item) {
        DateTime now = new DateTime();
        if (last.plus(duration).isBefore(now)) {
            boolean match = super.match(item);
            if (match) {
                last = now;
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Timed" + super.toString();
    }
}
