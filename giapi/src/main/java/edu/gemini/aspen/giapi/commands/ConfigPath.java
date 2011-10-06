package edu.gemini.aspen.giapi.commands;

/**
 * A ConfigPath can be used to refer to a set of items
 * in a Configuration tree. It is modeled after the <code>PioPath</code>
 * in the Gemini OCS.
 * <br>
 * The path is composed of a series of names separated by the ':' character
 */
public final class ConfigPath implements Comparable<ConfigPath> {

    private static final String SEPARATOR = ":";
    private static final char SEPARATOR_CHAR = ':';
    private static final String ITEM_SEPARATOR = ".";
    private static final String EMPTY_PATH_STR = "";
    private final String _path;
    private final int _prefixLength;

    /**
     * A ConfigPath to represent an empty path
     */
    public static final ConfigPath EMPTY_PATH = new ConfigPath(EMPTY_PATH_STR);

    /**
     * Factory constructor
     *
     * @param path The string representing the current path
     * @return a fully valid {@link ConfigPath}
     */
    public static ConfigPath configPath(String path) {
        return new ConfigPath(path);
    }

    /**
     * Creates a path for the given string as a parent and
     * the given child.
     *
     * @param parent String representation of the parent for
     *               the new ConfigPath
     * @param child  child of the config path
     * @return a fully valid {@link ConfigPath}
     */
    public static ConfigPath configPath(String parent, String child) {
        return new ConfigPath(parent, child);
    }

    /**
     * Creates a path for the given string
     *
     * @param path The string representing the current path
     */
    public ConfigPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        _path = _normalize(path);
        _prefixLength = _path.lastIndexOf(SEPARATOR_CHAR);
    }

    /**
     * Creates a path for the given string as a parent and
     * the given child.
     *
     * @param parent String representation of the parent for
     *               the new ConfigPath
     * @param child  child of the config path
     */
    ConfigPath(String parent, String child) {
        if (child == null) {
            throw new IllegalArgumentException("child cannot be null");
        }

        String path;
        if (parent == null) {
            path = child;
        } else {
            StringBuilder sb = new StringBuilder(parent);
            if (!(parent.endsWith(SEPARATOR) || (child.startsWith(SEPARATOR)))) {
                sb.append(SEPARATOR);
            }
            sb.append(child);
            path = sb.toString();
        }

        _path = _normalize(path);
        _prefixLength = _path.lastIndexOf(SEPARATOR_CHAR);
    }

    /**
     * Creates a new config path using the given
     * ConfigPath as a parent and the string as a child
     *
     * @param parent ConfigPath to be used as the parent for the
     *               new ConfigPath
     * @param child  child of the config path
     */
    ConfigPath(ConfigPath parent, String child) {
        this(parent.getName(), child);
    }

    /**
     * Split the path into its constituent parts
     *
     * @return names that make up the path
     */
    String[] split() {
        String path = _path;
        if (_path.startsWith(SEPARATOR)) {
            path = _path.substring(1);
        }
        return path.split(SEPARATOR);
    }

    /**
     * Get the name of the element referenced by this path. This
     * is just the last name in the path's sequence. For instance, if
     * the path is "gpi:cc:filter.name", the name is "filter.name"
     * If the path's sequence is empty, then the empty string
     * is returned.
     *
     * @return name of the element denoted by this path, or the
     *         empty string if this path's name sequence is empty
     */
    String getReferencedName() {
        if (_prefixLength < 0) {
            return _path;
        }
        return _path.substring(_prefixLength + 1);
    }

    /**
     * Get the path to the parent (if there is one) for this path.
     *
     * @return path for the parent node of this path, or
     *         <code>EMPTY_PATH</code> if this does not have a parent
     */
    public ConfigPath getParent() {
        if (_prefixLength <= 0) {
            return EMPTY_PATH;
        }
        return new ConfigPath(_path.substring(0, _prefixLength));
    }

    /**
     * Returns true if this path starts with the given path
     *
     * @param path base path to be tested
     * @return true if this path starts with the given path.
     */
    boolean startsWith(ConfigPath path) {
        if (path == null) {
            return false;
        }
        //check if the path starts with the given path
        return _path.startsWith(path.getName());

    }

    /**
     * Return the child path, assuming the given path is part of this
     * ConfigPath.
     * <br>
     * For instance, if this path is "gpi:cc:filter.name", a call
     * to <code>getChildPath(new ConfigPath("gpi"))</code> will
     * return a path to "gpi:cc".
     *
     * @param path parent path to get the child path in the current path
     * @return the child path for the given path argument, or
     *         <code>null</code> if it does not exist.
     */
    ConfigPath getChildPath(ConfigPath path) {
        if (!startsWith(path)) {
            return EMPTY_PATH;
        }
        if (path.equals(this)) {
            return EMPTY_PATH;
        }

        String rest = _path.substring(path._path.length());

        if (rest.startsWith(SEPARATOR)) {
            rest = rest.substring(1);
        }

        //if the rest starts with the item separator, then
        //there are no more paths, just the item. 
        if (rest.startsWith(ITEM_SEPARATOR)) {
            return null;
        }

        //if rest contains ".", remove that part.
        int itemPos = rest.indexOf(ITEM_SEPARATOR);
        if (itemPos > 0) {
            rest = rest.substring(0, itemPos);
        }

        String[] parts = rest.split(SEPARATOR);

        if (parts.length > 0) {
            return new ConfigPath(path, parts[0]);
        }
        return new ConfigPath(path, rest);
    }

    @Override
    public int compareTo(ConfigPath that) {
        return _path.compareTo(that._path);
    }

    /**
     * Returns the ConfigPath as a String
     *
     * @return string representation of this ConfigPath
     */
    public String getName() {
        return _path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigPath that = (ConfigPath) o;

        if (_prefixLength != that._prefixLength) {
            return false;
        }
        if (!_path.equals(that._path)) {
            return false;
        }
        //they are equals. 
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = _path.hashCode();
        result = 31 * result + _prefixLength;
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Auxiliary method to normalize a string to use it as a ConfigPath
     *
     * @param pathstr string to be normalized
     * @return normalized string to be used as a ConfigPath
     */
    private String _normalize(String pathstr) {
        if (pathstr == null || pathstr.isEmpty() || pathstr.equals(SEPARATOR)) {
            return EMPTY_PATH_STR;
        }
        StringBuilder sb = new StringBuilder();

        String[] parts = pathstr.trim().split(SEPARATOR);
        for (String part : parts) {
            part = part.trim();
            if ("".equals(part)) {
                continue;
            }
            sb.append(part).append(SEPARATOR);
        }
        String finalPathStr = sb.toString();
        //return the path without the last separator.
        return finalPathStr.substring(0, Math.max(finalPathStr.length() - 1, 0));
    }

}


