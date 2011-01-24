package edu.gemini.jms.api;

/**
 * Class JmsSimpleMessageSelector  is just a wrapper around a String selector.
 *
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 27, 2010
 */
public class JmsSimpleMessageSelector implements JmsMessageSelector{

    private final String _selector;

    /**
     * Create a simple message selector from a String
     * @param selector  the String to be used as selector
     */
    public JmsSimpleMessageSelector(String selector) {
        _selector=selector;
    }

    public String getSelectorString(){
        return _selector;
    }
}
