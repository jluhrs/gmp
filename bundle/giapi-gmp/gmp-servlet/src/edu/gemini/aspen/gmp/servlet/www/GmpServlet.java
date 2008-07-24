package edu.gemini.aspen.gmp.servlet.www;

import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 */
public class GmpServlet extends HttpServlet {


    private static final Logger LOG = Logger.getLogger(GmpServlet.class.getName());

    private GMPService _service;

    public GmpServlet(GMPService service) {
        _service = service;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        _processRequest(httpServletRequest, httpServletResponse);
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        _processRequest(httpServletRequest, httpServletResponse);
    }


    private void _processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.info("Received request " + req);
        HttpCommandRequest request;
        try {
            request = new HttpCommandRequest(req);
        } catch (BadRequestException ex) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        HandlerResponse response = _service.sendSequenceCommand(request.getSequenceCommand(), request.getActivity());



        String data = "Command Sent: " + request.getSequenceCommand().getName() + " Activity : " + request.getActivity().getName();
        if (response != null) {
            data += "Received answer = " + response.getResponse().getTag();
        }
        
        sendResponse(res, data);

        
    }

    public static void sendResponse(HttpServletResponse res, String data) {

        res.setContentType("text/html");

        BufferedOutputStream bos = null;
        try {
            OutputStream out = res.getOutputStream();
            bos = new BufferedOutputStream(out);
            bos.write(data.getBytes());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Problem sending response", ex);
        } finally {
            try {
                if (bos != null) bos.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Problem closing response", ex);
            }
        }
    }

}
