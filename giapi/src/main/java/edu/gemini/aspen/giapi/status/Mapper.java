package edu.gemini.aspen.giapi.status;

/**
 * This interface defines a mapper to extract information from
 * a status item to a domain type. Simple mappers will extract
 * basic types, like integers, doubles, etc.
 * <br>
 * Specific mappers can be defined to extract information using
 * domain-specific types. For instance, a FilterMapper can
 * be defined to extract a Filter (an enumerated type for instance)
 * out of the information encoded in the status item (normally
 * a String for these cases).
 * 
 */
public interface Mapper<S,T> {

    /**
     * Extract T from the provided StatusItem, or return
     * <code>null</code> if it's not possible.
     * @param item The status item to extract the information from
     * @return T or null if the value is not available. 
     */
    T extract (StatusItem<S> item);

}
