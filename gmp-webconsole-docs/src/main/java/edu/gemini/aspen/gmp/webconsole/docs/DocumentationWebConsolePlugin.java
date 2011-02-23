package edu.gemini.aspen.gmp.webconsole.docs;

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
@Instantiate(name = "Documentation Web Console")
@Provides
public class DocumentationWebConsolePlugin extends AbstractWebConsolePlugin {
    @ServiceProperty(name = "felix.webconsole.label", value = "documentation")
    private final String label = "documentation";

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    public String getLabel() {
        return "Help/Docs";
    }

    @Override
    public String getTitle() {
        return "Help/Docs";
    }
}
