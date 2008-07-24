package edu.gemini.util.obr.osgi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import edu.gemini.util.obr.AppServlet;
import edu.gemini.util.obr.DocServlet;
import edu.gemini.util.obr.ObrServlet;

public class Activator implements BundleActivator, ServiceTrackerCustomizer {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Activator.class.getName());
	private static final String PROP = "edu.gemini.util.obr.root";
	
	private BundleContext context;
	private ServiceTracker tracker;
	private ObrServlet servlet;
	private AppServlet appServlet;
	private DocServlet docServlet;
	private File root;
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		String _root = context.getProperty(PROP);
		if (_root == null)
			throw new IllegalStateException("You need to set the bundle property " + PROP + " to the filesystem root of your bundle tree.");
		
		root = new File(_root);
		if (root.exists() && root.isDirectory()) {	
			servlet = new ObrServlet(new File(root, "bundle"));
			appServlet = new AppServlet(new File(root, "app"));
			docServlet = new DocServlet(new File(root, "doc"), new File(root, "bundle"));
			tracker = new ServiceTracker(context, HttpService.class.getName(), this);
			tracker.open();
		} else {
			throw new IllegalArgumentException("The root " + root.getPath() + " does not exist or is not a directory.");
		}
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
		tracker = null;
		servlet = null;
		appServlet = null;
		docServlet = null;
		root = null;
		this.context = null;
	}

	public Object addingService(ServiceReference ref) {
		HttpService http = (HttpService) context.getService(ref);
		try {
			http.registerServlet("/bundle", servlet, new Hashtable(), null);
			http.registerServlet("/app", appServlet, new Hashtable(), null);
			http.registerResources("/runtime", new File(root, "runtime").getPath(), new FileSystemHttpContext());
			http.registerServlet("/doc", docServlet, new Hashtable(), null);
			return http;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void modifiedService(ServiceReference ref, Object service) {
	}

	public void removedService(ServiceReference ref, Object service) {
		HttpService http = (HttpService) service;
		http.unregister("/bundle");
		http.unregister("/app");
		http.unregister("/runtime");
		http.unregister("/doc");
		context.ungetService(ref);
	}

	private static class FileSystemHttpContext implements HttpContext {

		public boolean handleSecurity(HttpServletRequest req, HttpServletResponse res) throws IOException {
			return true;
		}

		public URL getResource(String path) {
			try {
				return new File(path).toURL();
			} catch (MalformedURLException e) {				
				return null;
			}
		}

		public String getMimeType(String path) {
			return null;
		}
		
	}
	
}
