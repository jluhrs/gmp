package edu.gemini.util.obr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.servlet.VelocityServlet;

@SuppressWarnings("serial")
public class AppServlet extends VelocityServlet {

	private static final Logger LOGGER = Logger.getLogger(AppServlet.class.getName());
	
	private final File root;
	
	public AppServlet(File root) {
		this.root = root;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Template handleRequest(HttpServletRequest req, HttpServletResponse res, Context context) throws Exception {

		String path = req.getPathInfo();
		if (path == null) {
			return null; // ?
		}

		String base = String.format("%s://%s:%d/bundle", req.getScheme(), req.getServerName(), req.getServerPort());
		
		BundleRepository obr = new BundleRepository(base, new File(root.getParentFile(), "bundle"));
		context.put("obr", obr);

		for (Entry<String, String[]> e: ((Map<String, String[]>) req.getParameterMap()).entrySet()) {
			String[] vals = e.getValue();
			context.put(e.getKey(), vals.length == 1 ? vals[0] : vals);
		}
		
		res.setContentType("text/plain");
		try {

			File realFile = new File(root, path.substring(1));
			if (realFile.isDirectory()) {
				
				// create a listing of files (not subdirs, for now);
				PrintWriter out = res.getWriter();				
				for (File f: realFile.listFiles()) {
					if (f.isFile())
						out.print(f.getName() + "\n");
				}
				return null;
				
			} else {			
				return getTemplate(path);
			}
		} catch (ResourceNotFoundException rnfe) {
			res.setStatus(404);
			return null;
		}

	}

	@Override
	protected Properties loadConfiguration(ServletConfig config) throws IOException, FileNotFoundException {
		Properties p = new Properties();		
		p.put("file.resource.loader.path", root.getPath());		
		p.put(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, DelegatingLogSystem.class.getName());
		return p;
	}

	public static class DelegatingLogSystem implements LogSystem {			

		public void logVelocityMessage(int level, String msg) {
			switch (level) {
				case DEBUG_ID: LOGGER.finer(msg); break;
				case ERROR_ID:
					if (!msg.contains("unable to find resource")) {
						LOGGER.severe(msg); break;
					} // else treat it as info
				case INFO_ID: LOGGER.fine(msg); break;
				case WARN_ID: LOGGER.warning(msg); break;
				default:
					LOGGER.info("Unknown level (" + level + "): " + msg);
			}
		}
	
		public void init(RuntimeServices rs) throws Exception {
		}
	
	};

	
}
