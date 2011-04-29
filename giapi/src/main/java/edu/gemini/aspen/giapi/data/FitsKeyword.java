package edu.gemini.aspen.giapi.data;

import com.google.common.base.Preconditions;

/**
 * Class representing a FitsKeyword
 * Non conforming keywords are not allowed
 */
public class FitsKeyword {
    private final String name;

    public FitsKeyword(String name) {
        Preconditions.checkArgument(name != null);
        Preconditions.checkArgument(!name.isEmpty());

        // TODO setup all the FITS restrictions before constructing
        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "FitsKeyword{" +
                "name='" + name + '\'' +
                '}';
    }
}
