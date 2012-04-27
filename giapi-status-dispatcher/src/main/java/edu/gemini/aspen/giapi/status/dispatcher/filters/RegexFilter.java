package edu.gemini.aspen.giapi.status.dispatcher.filters;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.StatusItemFilter;

import java.util.regex.Pattern;

/**
 * Class RegexFilter implements a filter based on a regular expression to be matched with the status item names
 *
 * @author Nicolas A. Barriga
 *         Date: 4/26/12
 */
public class RegexFilter implements StatusItemFilter {
    private final Pattern p;

    /**
     * This filter will match against a regexp pattern. It will match the entire name, so '^' and '$' are not needed to
     * indicate beginning and end of string.
     *
     * @param pattern to match the item names to. If there is more than one, they will get combined with the '|' (OR) operator.
     */
    public RegexFilter(String... pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            sb.append("(").append(pattern[i]).append(")");
            if (i < pattern.length - 1) {
                sb.append("|");
            }
        }
        this.p = Pattern.compile(sb.toString());
    }

    @Override
    public boolean match(StatusItem item) {
        return p.matcher(item.getName()).matches();
    }

    @Override
    public String toString() {
        return "RegexFilter: " + p.toString();
    }
}
