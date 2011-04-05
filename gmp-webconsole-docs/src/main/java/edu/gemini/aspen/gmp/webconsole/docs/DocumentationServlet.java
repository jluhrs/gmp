package edu.gemini.aspen.gmp.webconsole.docs;

import org.eclipse.jetty.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DocumentationServlet extends DefaultServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);    //To change body of overridden methods use File | Settings | File Templates.
        System.out.println("HEEE");
        System.out.println("HEEE");
    }
}
