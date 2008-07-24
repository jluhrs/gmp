package edu.gemini.oscar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class Application {

	private final String profile;	
//	private final File ocsDir, hostDir;
	private final File appDir, bundleDir, confDir, logDir, aboutTxt;
	private final File bundleProperties, loggingProperties, systemProperties, policy, log4jProperties;
	private String source;
	
	public String getSource() {
		return source;
	}

	public Application(String profile, File rootDir) throws UnknownHostException {
		this.profile = profile;

		if (rootDir == null) {
			File ocsDir = new File(System.getProperty("user.home"), ".ocs");		
			rootDir = new File(ocsDir, InetAddress.getLocalHost().getHostName());
		}
				
		// Construct the directories.
		appDir = new File(rootDir, profile);
		
//		System.out.println("Appdir is " + appDir);
		
		bundleDir = new File(appDir, "bundle");
		confDir = new File(appDir, "conf");
		logDir = new File(appDir, "log");
		
		// And the files.
		bundleProperties = new File(confDir, "bundle.properties");
		loggingProperties = new File(confDir, "logging.properties");
		systemProperties = new File(confDir, "system.properties");
		aboutTxt = new File(confDir, "about.txt");
		policy = new File(confDir, "policy");
		log4jProperties = new File(confDir, "log4j.properties");

	}
	
	
	public void install(String host, int port, String name, boolean verbose) throws IOException {

		// Create them directories if needed.
//		mkdirs(ocsDir, hostDir, appDir, bundleDir, confDir, logDir);
		mkdirs(appDir, bundleDir, confDir, logDir);

		// Fetch the config files.
		String root = "http://" + host + ":" + port + "/app/" + name;
		
		// Get a file listing
		if (verbose) System.out.println("Installing into " + appDir.getPath());
		InputStream is = new URL(root).openStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String fn;
		while ((fn = br.readLine()) != null) {
			if (verbose) System.out.print("     conf/" + fn + " ");
			fetch(root, fn, confDir, "logdir", esc(logDir.getPath()), "base", esc(appDir.getPath()));
			if (verbose) System.out.println(" [ok]");
		}
		
//		// Download the config files if possible.
//		
//		
//		
//		fetch(root, "bundle.properties", confDir);
//		fetch(root, "logging.properties", confDir, "logdir", logDir.getPath());
//		fetch(root, "system.properties", confDir, "base", appDir.getPath());
//		fetch(root, "about.txt", confDir);
//		fetch(root, "policy", confDir);
//		fetch(root, "log4j.properties", confDir);
//		fetch(root, "ocs.release.properties", confDir);
				
	}
	
	public String esc(String s) {
		return s.replace("\\", "\\\\");
	}
	
	public void uninstall() {
		rm(appDir);
	}
	
	private void rm(File f) {
		if (f.isFile()) {
			f.delete();
		} else {
			for (File c: f.listFiles()) {
				rm(c);
			}
			f.delete();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void fetch(String root, String filename, File dest, String... args) throws IOException {

		File props = new File(dest, filename);
		if (props.exists())
			props.delete();
		try {
			
			StringBuilder surl = new StringBuilder();
			surl.append(root).append("/").append(filename);
			for (int i = 0; i < args.length; i+=2) {
				surl.append(i == 0 ? '?' : '&');
				surl.append(URLEncoder.encode(args[i]));
				surl.append("=");
				surl.append(URLEncoder.encode(args[i+1]));
			}
			URL u = new URL(surl.toString());
			
			InputStream is = u.openStream();
			OutputStream os = new FileOutputStream(props);
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			os.close();
			is.close();

		} catch (FileNotFoundException fnfe) {
		}
	}
	
	private void mkdirs(File... files) throws IOException {
		for (File file: files) {
			if (file.exists() && file.isDirectory())
				continue;
			if (!file.mkdirs())
				throw new IOException("Could not make directory: " + file.getPath());
		}
	}

	public File getAppDir() {
		return appDir;
	}

	public File getBundleDir() {
		return bundleDir;
	}

	public File getBundleProperties() {
		return bundleProperties;
	}

	public File getBundleProperties(String config) {
		if (config == null) throw new IllegalArgumentException("Config can't be null.");
		return new File(bundleProperties.getParentFile(), bundleProperties.getName() + "." + config);
	}
	
	public File getConfDir() {
		return confDir;
	}

	public File getRootDir() {
		return getAppDir().getParentFile();
	}
	
//	public File getHostDir() {
//		return hostDir;
//	}

	public File getLogDir() {
		return logDir;
	}

	public File getLoggingProperties() {
		return loggingProperties;
	}

//	public File getOcsDir() {
//		return ocsDir;
//	}

	public File getPolicy() {
		return policy;
	}

	public String getProfile() {
		return profile;
	}

	public File getSystemProperties() {
		return systemProperties;
	}

	public File getSystemProperties(String config) {
		if (config == null) throw new IllegalArgumentException("Config can't be null.");
		return new File(systemProperties.getParentFile(), systemProperties.getName() + "." + config);
	}

	public File getAboutTxt() {
		return aboutTxt;
	}


	public File getLog4jProperties() {
		return log4jProperties;
	}
	
}
