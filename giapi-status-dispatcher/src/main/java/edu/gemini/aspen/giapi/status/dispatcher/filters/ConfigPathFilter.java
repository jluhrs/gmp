package edu.gemini.aspen.giapi.status.dispatcher.filters;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.StatusItemFilter;

/**
 * Class ConfigPathFilter implements hierarchical filtering.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/4/11
 */
public class ConfigPathFilter implements StatusItemFilter {
    private final ConfigPath configPath;

    /**
     * Constructs a filter  based on the given String.
     * <p/>
     * Trailing separators(":") are automatically removed. The filters "gpi:a" and "gpi:a:" are equivalent.
     * They will match any status items "gpi:a" and "gpi:a:anything".
     *
     * Beware that "gpi:a" will NOT match an item "gpi:a.1" since the parent of that item is "gpi:", not "gpi:a"
     *
     * @param filter
     */
    public ConfigPathFilter(String filter) {
        this.configPath = new ConfigPath(filter);
    }

    @Override
    public boolean match(StatusItem item) {
        for (ConfigPath path = new ConfigPath(item.getName()); !path.equals(ConfigPath.EMPTY_PATH); path = path.getParent()) {
            if (path.equals(configPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return configPath.equals(((ConfigPathFilter) o).configPath);
    }

    @Override
    public int hashCode() {
        return configPath.hashCode();
    }

    @Override
    public String toString() {
        return "ConfigPathFilter: "+configPath.toString();
    }

}
