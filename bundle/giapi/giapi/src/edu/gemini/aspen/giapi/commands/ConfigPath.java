package edu.gemini.aspen.giapi.commands;

/**
 *  A ConfigPath can be used to refer to a set of items
 * in a Configuration tree. It is modeled after the <code>PioPath</code>
 * in the Gemini OCS.
 * <p/>
 * The path is composed of a series of names separated by the ':' character
 * 
 */
public class ConfigPath implements Comparable<ConfigPath> {

    private static final String SEPARATOR = ":";
    private static final char SEPARATOR_CHAR = ':';
    private static final String ITEM_SEPARATOR = ".";
    private static final String EMPTY_PATH_STR = "";
    private String _path;
    private int _prefixLenght;

    /**
     * A ConfigPath to represent an empty path
     */
    public static final ConfigPath EMPTY_PATH = new ConfigPath(EMPTY_PATH_STR);


    /**
     * Creates a path for the given string
     * @param path The string representing the current path
     */
    public ConfigPath(String path)  {
        if (path == null) throw new NullPointerException();
        _path = _normalize(path);
        _prefixLenght = _path.lastIndexOf(SEPARATOR_CHAR);
    }

    /**
     * Creates a path for the given string as a parent and
     * the given child.
     * @param parent String representation of the parent for
     * the new ConfigPath
     * @param child child of the config path
     */
    public ConfigPath(String parent, String child) {

        if (child == null) throw new NullPointerException();

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
        _prefixLenght = _path.lastIndexOf(SEPARATOR_CHAR);
    }

    /**
     * Creates a new config path using the given
     * ConfigPath as a parent and the string as a child
     * @param parent ConfigPath to be used as the parent for the
     * new ConfigPath
     * @param child child of the config path
     */
    public ConfigPath(ConfigPath parent, String child) {
        this(parent.getName(), child);
    }


    /**
     * Split the path into its consitutent parts
     * @return names that make up the path
     */
    public String[] split() {
        if (SEPARATOR.equals(_path)) return new String[0];

        String path =_path;
        if (_path.startsWith(SEPARATOR)) {
            path = _path.substring(1);
        }
        return path.split(SEPARATOR);
    }

    /**
     * Get the name of the element referenced by this path. This
     * is just the last name in hte path's sequence. For instance, if
     * the path is "gpi:cc:filter.name", the name is "filter.name"
     * If the path's sequence is empty, then the empty string
     * is returned.
     *
     * @return name of the element denoted bu this path, or the
     * empty string if this path's name sequence is empty
     */
    public String getReferencedName() {
        if (_prefixLenght < 0) return _path;
        return _path.substring(_prefixLenght + 1);
    }

    /**
     * Get the path to the parent (if there is one) for this path.
     *
     * @return path for the parent node of this path, or
     * <code>null</code> if this does not have a parent
     */
    public ConfigPath getParent() {
        if (_prefixLenght <= 0) return null;
        return new ConfigPath(_path.substring(0, _prefixLenght));
    }

    /**
     * Returns true if this path starts with the given path
     * @param path base path to be tested
     * @return true if this path starts with the given path.
     */
    public boolean startsWith(ConfigPath path) {

        if (_path == null) return false;
        if (path == null) return false;
        //check if the path starts with the given path
        return _path.startsWith(path.getName());

    }

    /**
     * Return the child path, assuming the given path is part of this
     * ConfigPath.
     * <p/>
     * For instance, if this path is "gpi:cc:filter.name", a call
     * to <code>getChildPath(new ConfigPath("gpi"))</code> will
     * return a path to "gpi:cc".
     *
     *
     * @param path parent path to get the child path in the current path
     * @return the child path for the given path argument, or
     * <code>null</code> if it does not exist.
     */
    public ConfigPath getChildPath(ConfigPath path) {
        if (!startsWith(path)) return null;
        if (path.equals(this)) return null;

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


    public int compareTo(ConfigPath that) {
        return _path.compareTo(that._path);
    }

    /**
     * Returns the ConfigPath as a String
     * @return string representation of this ConfigPath
     */
    public String getName() {
        return _path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigPath that = (ConfigPath) o;

        if (_prefixLenght != that._prefixLenght) return false;
        if (_path != null ? !_path.equals(that._path) : that._path != null) return false;
        //they are equals. 
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (_path != null ? _path.hashCode() : 0);
        result = 31 * result + _prefixLenght;
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Auxiliary method to normalize a string to use it as a ConfigPath
     * @param pathstr string to be normalized
     * @return normalized string to be used as a ConfigPath
     */
    private String _normalize(String pathstr) {

        if (pathstr == null || "".equals(pathstr)) return "";
        pathstr = pathstr.trim();
        StringBuilder sb = new StringBuilder();

        String[] parts = pathstr.split(SEPARATOR);
        for (String part: parts) {
            part = part.trim();
            if ("".equals(part)) continue;
            sb.append(part).append(SEPARATOR);
        }
        pathstr = sb.toString();
        //return the path without the last separator.
        return pathstr.substring(0, pathstr.length() - 1);
    }

}


