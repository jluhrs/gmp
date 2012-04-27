package edu.gemini.aspen.giapi.status.dispatcher.filters;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.StatusItemFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class ListFilter implements a filter based on exact matching of a list of status item names
 *
 * @author Nicolas A. Barriga
 *         Date: 4/26/12
 */
public class ListFilter implements StatusItemFilter {
    private final Set<String> filters;

    public ListFilter(String... filters) {
        this.filters = new HashSet<String>(Arrays.asList(filters));
    }

    @Override
    public boolean match(StatusItem item) {
        return filters.contains(item.getName());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String f : filters) {
            s.append(f).append(" ");
        }
        return "ListFilter: " + s;
    }
}
