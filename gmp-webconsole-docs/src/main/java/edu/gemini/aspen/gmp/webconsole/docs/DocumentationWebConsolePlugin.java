package edu.gemini.aspen.gmp.webconsole.docs;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedMap;

/**
 * UI For the module in the form of a plugin for Felix Web Console
 */
@Component
@Instantiate(name = "Documentation Web Console")
@Provides
public class DocumentationWebConsolePlugin extends SimpleWebConsolePlugin {
    private static final String TITLE = "Help/Docs";
    @ServiceProperty(name = "felix.webconsole.label", value = "documentation")
    private final String label = "documentation";
    private String template;

    public DocumentationWebConsolePlugin() {
        super(TITLE, TITLE, null);


    }

    protected void renderContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
        template = readTemplateFile("/templates/documentation.html");

        // extract the configuration pid from the request path
        String pid = request.getPathInfo().substring(this.getLabel().length() + 1);
        System.out.println(request.getPathInfo());
        // check whether the pid is actually a filter for the selection

        response.getWriter().print(template);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // let's check for a json request
        final String info = request.getPathInfo();
        System.out.println(info);
        /*if ( info.endsWith( ".json" ) )
        {
            response.setContentType( "application/json" );
            response.setCharacterEncoding( "UTF-8" );

            // after last slash and without extension
            String pid = info.substring( info.lastIndexOf( '/' ) + 1, info.length() - 5 );
            // check whether the pid is actually a filter for the selection
            // of configurations to display, if the filter correctly converts
            // into an OSGi filter, we use it to select configurations
            // to display
            String pidFilter = request.getParameter( PID_FILTER );
            if ( pidFilter == null )
            {
                pidFilter = pid;
            }
            try
            {
                getBundleContext().createFilter( pidFilter );

                // if the pidFilter was set from the pid, clear the pid
                if ( pid == pidFilter )
                {
                    pid = null;
                }
            }
            catch ( InvalidSyntaxException ise )
            {
                // its ok, if the pid is just a single PID
                pidFilter = null;
            }

            final ConfigurationAdmin ca = this.getConfigurationAdmin();

            final Locale loc = getLocale( request );
            final String locale = ( loc != null ) ? loc.toString() : null;

            final PrintWriter pw = response.getWriter();

            try {
                pw.write("[");
                final SortedMap services = this.getServices(pid, pidFilter, locale, false);
                final Iterator i = services.keySet().iterator();
                boolean printColon = false;
                while ( i.hasNext() ) {
                    final String servicePid = i.next().toString();

                    final Configuration config = getConfiguration(ca, servicePid);
                    if ( config != null ) {
                        if ( printColon ) {
                            pw.print(',');
                        }
                        this.printConfigurationJson(pw, servicePid, config, pidFilter, locale);
                        printColon = true;
                    }
                }
                pw.write("]");
            } catch (InvalidSyntaxException e) {
                // this should not happened as we checked the filter before
            }
            // nothing more to do
            return;
        }*/

        super.doGet(request, response);
    }

}
