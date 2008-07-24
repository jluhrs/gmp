package edu.gemini.oscar;

import org.ungoverned.oscar.Main;
import org.ungoverned.oscar.util.DefaultBundleCache;
import org.ungoverned.oscar.util.OscarConstants;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Launcher {

	public static final String CONFIG_PROP = Launcher.class.getName() + ".config";
	public static final String SHARED_PROP = Launcher.class.getName() + ".shared";

	@SuppressWarnings({"unused", "UnusedDeclaration"})
        private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());
	private static File root;

	public static void main(String[] args) throws Exception {

		boolean help = false, noninteractive = false, uninstall = false, verbose = false, run = true;
		String alias = null;
		String install = null;
		String host = "localhost";
		boolean shared = false;
		int port = 9999;
		int level = Integer.MAX_VALUE;
		Map<String, String> bundleProps = new TreeMap<String, String>(), sysProps = new TreeMap<String, String>();
//		File root = null;
		String config = null;

		for (String opt: args) {

			// All options start with a
			if (opt.charAt(0) != '-') die("I think you forgot a dash somewhere.");

			// Many have two parts. First has to be a single character.
			String[] parts = opt.substring(1).split(":");
			if (parts.length == 0) die("I don't know what you mean when you say: " + opt);
			String key = parts[0];
			if (parts.length == 0) die("I was totally not expecting more than one letter here: -" + key);
			String val = parts.length == 1 ? null : parts[1];

			// Ok, now we have a key/value pair.
			switch (key.charAt(0)) {

			case '?':
				help = true;
				run = false;
				break;

			case 'a':
				if (val == null) die("Yes, but which application do you mean? Try -a:<appname>");
				alias = val;
				break;

			case 't':
				showTableOfContentsAndDie();
				break;

			case 'i':
				if (val == null) die("Install what? Try -i:<appname>");
				install = val;
				run = false;
				break;

			case 'n':
				noninteractive = true;
				break;

			case 'u':
				uninstall = true;
				run = false;
				break;

			case 'h':
				if (val == null) die("Which host? Try -h:<host>");
				host = val;
				break;

			case 'p':
				if (val == null) die("Right, but which port exactly? Try -h:<port>");
				try {
					port = Integer.parseInt(val);
				} catch (NumberFormatException nfe) {
					die("The port needs to be a number, amigo.");
				}
				break;

			case 'v':
				System.out.println("Arguments were: " + Arrays.toString(args));
				verbose = true;
				break;

			case 'b': {
				die("Sorry, this option is disabled in this version."); //////////// *****************
				if (val == null) die("Which bundle property should I set? Try -b:<key>=<value>");
				String[] kv = val.split("=");
				if (kv.length == 0) die("Which bundle property should I set? Try -b:<key>=<value>");
				bundleProps.put(kv[0], kv[1] != null ? kv[1] : "");
				run = false;
				break; }

			case 's': {
				if (val == null) die("Which system property should I set? Try -s:<key>=<value>");
				String[] kv = val.split("=");
				if (kv.length == 0) die("Which system property should I set? Try -s:<key>=<value>");
				sysProps.put(kv[0], kv[1] != null ? kv[1] : "");
				run = false;
				break; }

			case 'L':
				if (val == null) die("Which level, then? Try -L:<number>");
				try {
					level = Integer.parseInt(val);
					run = false;
				} catch (NumberFormatException nfe) {
					die("The level needs to be a number, huevon.");
				}
				break;

			case 'S':
				if (val == null) val = System.getProperty(SHARED_PROP);
				if (val == null) die("Ok, shared repository is where?");

				root = new File(val);				
				if (verbose) System.out.println("Looking for shared repository in " + root.getAbsolutePath());
				if (!root.exists()) die("Hmm, the shared repository doesn't exist. Remember that I don't know how to expand ~foo.");				

				shared = true;
				System.setProperty(SHARED_PROP, root.getAbsolutePath());
				break;

			case 'c':
				if (val == null) die("Which config? Try -c:test for example.");
				config = val;
				if (verbose) System.out.println("Using config " + config);
				System.setProperty(CONFIG_PROP, config);
				break;

			}

		}

		// Main Help
		if (help && alias == null) {
			usage();
			return;
		}

		// Some sanity checks
		if (alias == null && install == null) die("Sorry, what application are we talking about? You need -a or -i, or both.");

		// Use the install name for the alias, if not given.
		if (alias == null) alias = install;

		// Can make the app now
		Application app = new Application(alias, root);

		// App help
		if (help) {
			if (app.getAboutTxt().exists()) {
				show(new FileInputStream(app.getAboutTxt()));
			} else {
				System.out.println("Sorry, there is no application-specific help for " + alias);
			}
			return;
		}

		// Try uninstall
		if (uninstall) {
			if (app.getAppDir().exists()) {
				if (verbose) System.out.print("Uninstalling " + app.getAppDir() + "... ");
				app.uninstall();
				if (verbose) System.out.println("done.");
			} else {
				die("Can't install what ain't there, chief.");
			}
		}

		// Now try install
		if (install != null) {
			app.install(host, port, install, verbose);
		}

		// Bundle Prop Updates
		if (!bundleProps.isEmpty()) {
			Properties props = new Properties();
			if (app.getBundleProperties().exists()) {
				props.load(new FileInputStream(app.getBundleProperties()));
			}
			props.putAll(bundleProps);
			props.store(new FileOutputStream(app.getBundleProperties()), "Updated by Launcher, whee.");
			if (verbose) System.out.println("Set " + bundleProps.size() + " bundle prop(s).");
		}

		// System Prop Updates
		if (!sysProps.isEmpty()) {
			Properties props = new Properties();
			if (app.getBundleProperties().exists()) {
				props.load(new FileInputStream(app.getSystemProperties()));
			}
			props.putAll(sysProps);
			props.store(new FileOutputStream(app.getSystemProperties()), "Updated by Launcher, whee.");
			if (verbose) System.out.println("Set " + sysProps.size() + " system prop(s).");
		}

		// Auto-Start Level
		if (level != Integer.MAX_VALUE) {
			if (app.getSystemProperties().exists()) {
				Properties props = new Properties();
				props.load(new FileInputStream(app.getSystemProperties()));
				Pattern pat = Pattern.compile("oscar\\.auto\\.start\\.(\\d+)(\\.disabled)?");
				for (Entry e: new HashSet<Entry>(props.entrySet())) {
					String key = (String) e.getKey();
					String val = (String) e.getValue();
					Matcher m = pat.matcher(key);
					if (m.matches()) {
						int d = Integer.parseInt(m.group(1));
						boolean enabled = m.group(2) == null;
						if (d > level && enabled) {
							props.remove(key);
							props.put(key + ".disabled", val);
						} else if (d <= level && !enabled) {
							props.remove(key);
							props.put("oscar.auto.start." + d, val);
						}
					}
				}
				props.store(new FileOutputStream(app.getSystemProperties()), "Updated by Launcher. Blah.");
				if (verbose) System.out.println("Changed auto-start level to " + level);
			} else {
				die("I don't have an information about auto-start for " + alias);
			}
			return;
		}


		// Finally, run the app if we need to.
		if (run) {

			if (!app.getBundleDir().exists()) {
				die("I don't know about " + app.getProfile() + " ... try installing it first.");
				return;
			}

			if (app.getLoggingProperties().exists()) {
				if (verbose) System.out.println("Initializing JDK logging from " + app.getLoggingProperties().getPath());
				System.setProperty("java.util.logging.config.file", app.getLoggingProperties().getPath());
				LogManager.getLogManager().readConfiguration();
			} else {
				if (verbose) System.out.println("Using default JDK logging.");
			}

			if (app.getPolicy().exists()) {
				if (verbose) System.out.println("Initializing policy from " + app.getPolicy().getPath());
				System.setProperty("java.security.policy", app.getPolicy().getPath());
				System.setSecurityManager(new SecurityManager());
			} else {
				if (verbose) System.out.println("No security manager will be used.");
			}

			if (app.getLog4jProperties().exists()) {
				if (verbose) System.out.println("Initializing Log4j from " + app.getLog4jProperties().toURL().toExternalForm());
				System.setProperty("log4j.configuration", app.getLog4jProperties().toURL().toExternalForm());
			} else {
				if (verbose) System.out.println("Log4j will not be used.");
			}

//			if (app.getSystemProperties().exists()) {
//				if (verbose) System.out.println("Merging system properties from " + app.getSystemProperties().getPath());
//				System.setProperty(OscarConstants.SYSTEM_PROPERTIES_PROP, app.getSystemProperties().getPath());
//			} else {
//				if (verbose) System.out.println("No extra system properties.");
//			}


			if (app.getSystemProperties().exists()) {

				if (config == null || !app.getSystemProperties(config).exists()) {

					// system.properties only
					if (verbose) System.out.println("Merging system properties from  " + app.getSystemProperties().getPath());
					System.setProperty(OscarConstants.SYSTEM_PROPERTIES_PROP, app.getSystemProperties().getPath());

				} else {

					// system.properties + system.properties.<config>
					File temp = File.createTempFile("launcher-", ".properties");
					temp.deleteOnExit();
					if (verbose) System.out.println("Merging system properties: " + app.getSystemProperties().getPath());
					if (verbose) System.out.println("                         + " + app.getSystemProperties(config).getPath());
					if (verbose) System.out.println("                         = " + temp.getPath());

					Properties props = new Properties();
					props.load(new FileInputStream(app.getSystemProperties()));
					props.load(new FileInputStream(app.getSystemProperties(config)));
					props.store(new FileOutputStream(temp), "Merged properties from " + app.getSystemProperties().getPath() + " and " + app.getSystemProperties(config).getPath());
					System.setProperty(OscarConstants.SYSTEM_PROPERTIES_PROP, temp.getPath());

				}

			} else if (config != null && app.getSystemProperties(config).exists()) {

					// system.properties.<config> only
					if (verbose) System.out.println("Merging system properties from  " + app.getSystemProperties(config).getPath());
					System.setProperty(OscarConstants.SYSTEM_PROPERTIES_PROP, app.getSystemProperties(config).getPath());

			}




			if (app.getBundleProperties().exists()) {

				if (config == null || !app.getBundleProperties(config).exists()) {

					// bundle.properties only
					if (verbose) System.out.println("Merging bundle properties from  " + app.getBundleProperties().getPath());
					System.setProperty(OscarConstants.BUNDLE_PROPERTIES_PROP, app.getBundleProperties().getPath());

				} else {

					// bundle.properties + bundle.properties.<config>
					File temp = File.createTempFile("launcher-", ".properties");
					temp.deleteOnExit();
					if (verbose) System.out.println("Merging bundle properties: " + app.getBundleProperties().getPath());
					if (verbose) System.out.println("                         + " + app.getBundleProperties(config).getPath());
					if (verbose) System.out.println("                         = " + temp.getPath());

					Properties props = new Properties();
					props.load(new FileInputStream(app.getBundleProperties()));
					props.load(new FileInputStream(app.getBundleProperties(config)));
					props.store(new FileOutputStream(temp), "Merged properties from " + app.getBundleProperties().getPath() + " and " + app.getBundleProperties(config).getPath());
					System.setProperty(OscarConstants.BUNDLE_PROPERTIES_PROP, temp.getPath());

				}

			} else if (config != null && app.getBundleProperties(config).exists()) {

					// bundle.properties.<config> only
					if (verbose) System.out.println("Merging bundle properties from  " + app.getBundleProperties(config).getPath());
					System.setProperty(OscarConstants.BUNDLE_PROPERTIES_PROP, app.getBundleProperties(config).getPath());

			}

			if (verbose) System.out.println("Using bundles from  " + app.getSystemProperties().getPath());
			System.setProperty(DefaultBundleCache.CACHE_PROFILE_DIR_PROP, app.getBundleDir().getPath());


			if (noninteractive) {

				if (shared) {
                                    String prefix = alias + "-";
                                        File out = File.createTempFile(prefix, ".out");
					File err = File.createTempFile(prefix, ".err");
					System.out.println("Starting shared-mode " + app.getProfile());
					System.out.println("Your logfiles in " + out.getParentFile().getAbsolutePath() + " are " + err.getName() + " and " + out.getName());
					if (verbose) System.out.println("Redirecting output.  Goodbye.");
					System.setErr(new PrintStream(new FileOutputStream(out)));
					System.setOut(new PrintStream(new FileOutputStream(err)));
				} else {
					if (verbose) System.out.println("Redirecting output to " + app.getLogDir() + "/[err|out].log.  Goodbye.");
					System.setErr(new PrintStream(new FileOutputStream(new File(app.getLogDir(), "err.log"))));
					System.setOut(new PrintStream(new FileOutputStream(new File(app.getLogDir(), "out.log"))));
				}
			} else {
				if (verbose) System.out.println("Launching in interactive mode.");
			}

			Main.main(new String[0]); // does not return

		}


	}

	private static void usage() throws IOException {
		show(Launcher.class.getResourceAsStream("usage.txt"));
	}

	private static void show(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		for (String line = br.readLine(); line != null; line = br.readLine())
			System.out.println(line);
	}

	private static void die(String err) {
		System.err.println(err);
		System.err.println("Let me help you. Try: java -jar launcher.jar -?");
		System.exit(-1);
	}

	private static void showTableOfContentsAndDie() throws UnknownHostException {
		File rootDir = (root != null) ? root : new Application("", null).getAppDir();
		System.out.println("root is " + rootDir);
		for (File dir: rootDir.listFiles()) {
			System.out.println(dir.getName());
		}
		System.exit(0);
	}

}


