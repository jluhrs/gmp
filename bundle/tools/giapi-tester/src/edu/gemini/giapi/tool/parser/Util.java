package edu.gemini.giapi.tool.parser;

/**
 * Utility methods
 */
public class Util {

    private static String defaultMessage = null;

    /**
     * List the values an enum has, in a comma separated list format
     * @param e the enumerated type we need to get the values from
     * @return Comman separated list of the values contained in the
     *         enumerated type. 
     */
    public static <T extends Enum<T>> String getValues(Class<T> e) {
        StringBuilder sb = new StringBuilder();

        T[] elems = e.getEnumConstants();
        for (int i = 0; i < elems.length; i++) {
            sb.append(elems[i]);
            if (i < elems.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Exit the application with a message.
     * If a default message has been defined, it will
     * be displayed also.
     * @see #registerDefaultMessage(String)
     * @param arg The message to be displayed before exiting.
     */
    public static void die(String arg) {
        System.err.println(arg);
        if (defaultMessage != null) {
            System.err.println(defaultMessage);
        }
        System.exit(-1);
    }

    /**
     * Register a default message that will go along with
     * every messaged displayed when the die() method is
     * called.
     * @param msg the default message to be used.
     */
    public static void registerDefaultMessage(String msg) {
        defaultMessage = msg;
    }
    

}
