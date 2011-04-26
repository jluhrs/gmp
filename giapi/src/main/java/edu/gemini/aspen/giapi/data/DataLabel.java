package edu.gemini.aspen.giapi.data;

import com.google.common.base.Preconditions;

/**
 * A very simple data structure to represent a data label
 */
public final class DataLabel {

    private final String _name;

    public DataLabel(String name) {
        Preconditions.checkArgument(name != null && !"".equals(name.trim()), "A DataLabel name can't be null nor empty");
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataLabel dataLabel = (DataLabel) o;

        if (!_name.equals(dataLabel._name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public String toString() {
        return _name;
    }
}
