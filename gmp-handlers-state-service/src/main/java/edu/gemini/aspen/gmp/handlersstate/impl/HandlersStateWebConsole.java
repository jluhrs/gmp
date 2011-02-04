package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;
import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UI For the module in the form of a plugin for Felix Web Console
 */
@Component
@Instantiate(name = "HSS Web Console")
@Provides
public class HandlersStateWebConsole extends AbstractWebConsolePlugin {
    @ServiceProperty(name = "felix.webconsole.label", value = "handlers")
    private final String label = "handlers";

    @Requires(optional = false)
    private HandlersStateService handlerStateService;

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    public String getLabel() {
        return "Handlers State";
    }

    @Override
    public String getTitle() {
        return "Handlers State";
    }
}
