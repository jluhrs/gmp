package edu.gemini.aspen.gmp.data;

/**
 * A very simple data structure to represent a dataset
 */
public class Dataset {

    private String _name;

    public Dataset(String name) {
        if (name == null) {
            throw new IllegalArgumentException("A Dataset name can't be null");
        }
        _name = name;
    }

    public String getName() {
        return _name;
    }


}
