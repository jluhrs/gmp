package edu.gemini.util.obr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

@SuppressWarnings("serial")
public class ObrServlet extends HttpServlet {

	private final File root;
	
	public ObrServlet(File root) {
		this.root = root;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String path = req.getPathInfo();
		
		if (path == null || path.length() <= 1) {

			// Path is empty; return the bundle repository XML.
			
			String base = String.format("%s://%s:%d%s",
					req.getScheme(),
					req.getServerName(),
					req.getServerPort(),
					req.getServletPath());
			
			try {
				res.setContentType("text/xml");
				ObrDocumentGenerator oxg = new ObrDocumentGenerator(root, base);
				Document doc = oxg.createDocument();
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.transform(new DOMSource(doc), new StreamResult(res.getOutputStream()));
			} catch (Exception e) {
				throw new ServletException(e);
			}
			
		} else {
			
			// Path is non-empty. Return the resource
			try {
				OutputStream out = res.getOutputStream();
				InputStream is = new FileInputStream(root.getPath() + path);
				byte[] buf = new byte[1024 * 8]; // 8k blocks?
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					out.write(buf, 0, len);
				}
				out.flush();
				is.close();			
			} catch (FileNotFoundException fnfe) {
				res.sendError(404, "Not found.");
			}
			
		}
	
	}
	
}
