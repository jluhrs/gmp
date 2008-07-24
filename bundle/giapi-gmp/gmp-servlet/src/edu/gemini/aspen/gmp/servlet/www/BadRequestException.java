package edu.gemini.aspen.gmp.servlet.www;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Jul 14, 2008
 * Time: 6:27:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
