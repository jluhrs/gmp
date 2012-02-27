package edu.gemini.aspen.giapi.data;

import com.google.common.base.Preconditions;

import java.util.regex.Pattern;

/**
 * Class representing a FitsKeyword
 * Non conforming keywords are not allowed
 */
@Deprecated
public class FitsKeyword {
    public final static Pattern FITS_KEYWORD_PATTERN = Pattern.compile("[\\p{Upper}\\d-_]{1,8}");
    private final String name;

    public FitsKeyword(String name) {
        Preconditions.checkArgument(name != null);
        Preconditions.checkArgument(FITS_KEYWORD_PATTERN.matcher(name.toUpperCase()).matches());

        this.name = name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FitsKeyword that = (FitsKeyword) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "FitsKeyword{" +
                "name='" + name + '\'' +
                '}';
    }
}
