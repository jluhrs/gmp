package edu.gemini.util.obr;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ObrDocumentGenerator {

	private static final Logger LOGGER = Logger.getLogger(ObrDocumentGenerator.class.getName());
	
	private static final FileFilter FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".jar");
		}
	};

	private final File root;
	private final String base;
	private Document doc;
	private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private final DocumentBuilder db;

	public ObrDocumentGenerator(File root, String base) throws ParserConfigurationException {
		if (!root.isDirectory())
			throw new IllegalArgumentException("Root must be a directory.");
		this.root = root;
		this.base = base;
		db = dbf.newDocumentBuilder();
	}
	
	public Document createDocument() throws ParserConfigurationException {
		
		// <bundles>
		doc = db.newDocument();				
		Element docElement = appendElement(doc, "bundles");
		
		// <dtd-version>
		appendElement(docElement, "dtd-version").setTextContent("1.0");

		// <repository>
		Element repoElement = appendElement(docElement, "repository");
		appendElement(repoElement, "name").setTextContent("Gemini Bundle Repository");
		appendElement(repoElement, "url").setTextContent(base.toString());
		appendElement(repoElement, "date").setTextContent(new Date().toString());
		appendElement(repoElement, "extern-repositories");
		
		Queue<File> queue = new LinkedList<File>();
		queue.add(root);
		while (!queue.isEmpty()) {
			File f = queue.remove();
			if (f.isDirectory()) {
				if (!f.getName().equals("lib"))
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
						String src = loc.substring(0, loc.length() - 4) + "-src.jar";
						String doc = loc.substring(0, loc.length() - f.getName().length()) + "doc/index.html";
						
						// <bundle>
						Element be = appendElement(docElement, "bundle");
						appendElement(be, "Bundle-Name").setTextContent(attrs.getValue("Bundle-Name"));
						appendElement(be, "Bundle-Description").setTextContent(attrs.getValue("Bundle-Description"));
						appendElement(be, "Bundle-UpdateLocation").setTextContent(loc);
						appendElement(be, "Bundle-SourceURL").setTextContent(src);
						appendElement(be, "Bundle-Version").setTextContent(attrs.getValue("Bundle-Version"));
						appendElement(be, "Bundle-DocURL").setTextContent(doc);
						appendElement(be, "Bundle-Category").setTextContent(attrs.getValue("Bundle-Category"));
						addPackages(be, "import-package", attrs.getValue("Import-Package"));
						addPackages(be, "export-package", attrs.getValue("Export-Package"));
						
					}
				} catch (IOException ioe) {
					LOGGER.log(Level.WARNING, "Trouble with jarfile: " + f, ioe);
				}
				
			}
		}
		
		return doc;
	}	
	
	private void addPackages(Element parent, String name, String attr) {
		if (attr != null) {
			for (String item: attr.split("\\s*,\\s*")) {
				Element pe = null;
				for (String part: item.split("\\s*;\\s*")) {
					if (pe == null) {				
						pe = appendElement(parent, name);
						pe.setAttribute("package", part);
					} else{
						String[] kv = part.split("\\s*=\\s*");
						kv[1] = kv[1].replaceAll("\"", "");
						pe.setAttribute(kv[0], kv[1]);
					}
				}
			}
		}
	}
	
	/** Create and append an element in one step */
	private Element appendElement(Node parent, String name) {
		Element e = doc.createElement(name);
		parent.appendChild(e);
		return e;
	}
	
}
