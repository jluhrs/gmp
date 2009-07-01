package edu.gemini.aspen.gmp.data;

/**
 * A very simple data structure to represent a dataset
 */
public final class Dataset {

    private String _name;

    public Dataset(String name) {
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("A Dataset name can't be null nor empty");
        }
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dataset dataset = (Dataset) o;

        if (!_name.equals(dataset._name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }
}
