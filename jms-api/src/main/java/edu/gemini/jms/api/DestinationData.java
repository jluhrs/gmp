package edu.gemini.jms.api;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Container with JMS Destination information.
 */
public final class DestinationData {

    private String _name;
    private DestinationType _type;

    public DestinationData(String name, DestinationType type) {
        checkArgument(name != null, "Destination name cannot be null");
        checkArgument(!name.isEmpty(), "Destination name cannot be null");
        checkArgument(type != null, "Destination Type cannot be null");
        
        _name = name;
        _type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationData that = (DestinationData) o;

        if (_name != null ? !_name.equals(that._name) : that._name != null)
            return false;
        if (_type != that._type) return false;
        //objects are the same
        return true;
    }

    @Override
    public int hashCode() {
        int result = _name != null ? _name.hashCode() : 0;
        result = 31 * result + (_type != null ? _type.hashCode() : 0);
        return result;
    }

    /**
     * Return the name of the JMS destination
     * @return name of the Destination
     */
    public String getName() {
        return _name;
    }

    /**
     * Return the Destination type
     * @return Destination Type. 
     */
    public DestinationType getType() {
        return _type;
    }
}
