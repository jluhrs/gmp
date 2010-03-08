package edu.gemini.aspen.giapi.data.fileevents;

/**
 * Runtime exception for File Events. 
 */
public class FileEventException extends RuntimeException {
    
    public FileEventException(String message, Exception cause) {
        super(message, cause);
    }

    public FileEventException(String message) {
        super(message);
    }
}
