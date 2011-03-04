package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.commands.ConfigPath;

/**
 * Class StatusItemFilter
 *
 * @author Nicolas A. Barriga
 *         Date: 3/4/11
 */
public class StatusItemFilter {
    private final ConfigPath configPath;

    public static final StatusItemFilter EMPTY_FILTER = new StatusItemFilter(ConfigPath.EMPTY_PATH);

    private StatusItemFilter(ConfigPath filter){
        this.configPath=filter;
    }

    /**
     * Constructs a filter  based on the given String.
     *
     * Trailing separators(":") are automatically removed. The filters "gpi:a" and "gpi:a:" are equivalent.
     * They will match any status items "gpi:a" and "gpi:a:anything".
     *
     * @param filter
     */
    public StatusItemFilter(String filter){
        this.configPath=new ConfigPath(filter);
    }

    /**
     *
     * @return The parent of a filter, has the same name, minus the last section after a ":". Ex.: the parent of "gpi:a" is "gpi".
     */
    public StatusItemFilter getParent(){
        return new StatusItemFilter(configPath.getParent());
    }

    @Override
     public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return configPath.equals(((StatusItemFilter)o).configPath);
     }

     @Override
     public int hashCode() {
         return configPath.hashCode();
     }

     @Override
     public String toString() {
         return configPath.toString();
     }

}
