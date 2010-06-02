package edu.gemini.aspen.giapi.data;

import java.util.Map;
import java.util.HashMap;

/**
 * Definition of the GIAPI file events
 */
public enum FileEvent {

    /**
     * Ancillary file events
     */
    ANCILLARY_FILE(0),
    /**
     * Intermediate file events
     */
    INTERMEDIATE_FILE(1);


    /**
     * Integer code associated to each file event. Used for
     * encoding file events in messages
     */
    private int _code;

    private FileEvent(int code) {
        _code = code;
    }

    int getCode() {
        return _code;
    }


    private static Map<Integer, FileEvent> CONVERSION_TABLE = new HashMap<Integer, FileEvent>();

    /**
     * Map FileEvent cides to the corresponding enum values.
     */
    static {
        for (FileEvent event: FileEvent.values()) {
            CONVERSION_TABLE.put(event.getCode(), event);
        }
    }


    /**
     * Utility method to associate a "code" with a File Event. This is
     * a bit of
     * @param code code associated to the file event
     * @return the file event associated to the code or <code>null</code>
     * if the code does not match any existing File Event. 
     */
    public static FileEvent getByCode(int code) {
        return CONVERSION_TABLE.get(code);
    }



}
