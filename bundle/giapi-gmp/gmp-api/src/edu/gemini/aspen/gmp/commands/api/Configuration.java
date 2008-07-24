package edu.gemini.aspen.gmp.commands.api;

import java.util.Enumeration;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Jun 17, 2008
 * Time: 9:05:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {

    String getValue(String key);

    Enumeration<String> getKeys();

}
