package edu.gemini.util.obr;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BundleRepository {

	private static final Logger LOGGER = Logger.getLogger(BundleRepository.class.getName());

	private static final FileFilter FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".jar");
		}
	};

	private final Map<String, URL> urls = new TreeMap<String, URL>();
	private final String base;
	
	public BundleRepository(String base, File root) {
		this.base = base;
		Queue<File> queue = new LinkedList<File>();
		queue.add(root);
		while (!queue.isEmpty()) {
			File f = queue.remove();
			if (f.isDirectory()) {
				queue.addAll(Arrays.asList(f.listFiles(FILTER)));
			} else {
				try {
					JarFile jar = new JarFile(f);
					Manifest mf = jar.getManifest();
					Attributes attrs = mf.getMainAttributes();
					String name = attrs.getValue("Bundle-Name");
					if (name != null) {													
						String rpath = f.getPath().substring(root.getPath().length());
						String loc = base + rpath;						
						urls.put(name, new URL(loc));						
					}
				} catch (IOException ioe) {
					LOGGER.log(Level.WARNING, "Trouble with jarfile: " + f, ioe);
				}
				
			}
		}
		
	}

	public String root() {
		return base;
	}
	
	public String urls(List<String> names) {
		StringBuilder buf = new StringBuilder();
		for (Iterator<String> it = names.iterator(); it.hasNext(); ) {
			buf.append(getUrl(it.next()));
			if (it.hasNext())
				buf.append(" ");
		}
		return buf.toString();
	}
	
	public URL getUrl(String name) {
		return urls.get(name);
	}
	
}
