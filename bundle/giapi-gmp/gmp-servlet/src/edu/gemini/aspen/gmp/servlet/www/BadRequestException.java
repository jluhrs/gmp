package edu.gemini.aspen.gmp.servlet.www;

/**
 * An exception representing an invalid request
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
