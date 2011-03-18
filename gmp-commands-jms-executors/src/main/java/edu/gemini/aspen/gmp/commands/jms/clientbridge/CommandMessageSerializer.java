package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cquiroz
 * Date: 3/18/11
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommandMessageSerializer {
    static Map<String, String> convertHandlerResponseToProperties(HandlerResponse response) {
        Map<String, String> properties = Maps.newHashMap();

        properties.put(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
        if (response.hasErrorMessage()) {
            properties.put(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }
        return properties;
    }
}
