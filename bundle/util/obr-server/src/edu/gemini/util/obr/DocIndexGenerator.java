package edu.gemini.util.obr;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
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

public class DocIndexGenerator {

	private static final Logger LOGGER = Logger.getLogger(DocIndexGenerator.class.getName());
	
	private static final FileFilter FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".jar");
		}
	};

	private final File docRoot;
	private final File bundleRoot;
	private final String base;
	private Document doc;
	private final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private final DocumentBuilder db;

	public DocIndexGenerator(File docRoot, File bundleRoot, String base) throws ParserConfigurationException {
		if (!docRoot.isDirectory())
			throw new IllegalArgumentException("Root must be a directory.");
		this.docRoot = docRoot;
		this.base = base;
		this.bundleRoot = bundleRoot;
		db = dbf.newDocumentBuilder();
	}
	
	public Document createDocument() throws ParserConfigurationException {
		
		doc = db.newDocument();
		
		Element htmlElement = appendElement(doc, "html");
		Element headElement = appendElement(htmlElement, "head");
		
		Element styleElement = appendElement(headElement, "link");
		styleElement.setAttribute("rel", "stylesheet");
		styleElement.setAttribute("href", "/doc/styles.css");
		
		Element bodyElement = appendElement(htmlElement, "body");

		Element titleElement = appendElement(bodyElement, "h1");
		titleElement.setTextContent("Craptacular Bundle Doc Index");

		Element olElement = appendElement(bodyElement, "ol");		

		
		Queue<File> queue = new LinkedList<File>();
		queue.add(bundleRoot);
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
													
						String rpath = f.getPath().substring(bundleRoot.getPath().length());
						String loc = base + rpath;
						String src = loc.substring(0, loc.length() - 4) + "-src.jar";

						File dir = f.getParentFile();
						File docDir = new File(dir, "doc");
						File index = new File(docDir, "index.html");
						
//						System.out.println(index.getPath());
						
						String doc = loc.substring(0, loc.length() - f.getName().length()) + "doc/index.html";
						
						// <li>
						Element be = appendElement(olElement, "li");
//						appendElement(be, "bundle-name").setTextContent(attrs.getValue("Bundle-Name"));
//						appendElement(be, "bundle-descriptin").setTextContent(attrs.getValue("Bundle-Description"));
//						appendElement(be, "bundle-updatelocation").setTextContent(loc);
//						appendElement(be, "bundle-sourceurl").setTextContent(src);
//						appendElement(be, "bundle-version").setTextContent(attrs.getValue("Bundle-Version"));
						
						if (index.exists()) {
						
							Element link = appendElement(be, "a");
							link.setTextContent(attrs.getValue("Bundle-Name"));
							link.setAttribute("href", doc);

						} else {
							
							appendElement(be, "span").setTextContent(attrs.getValue("Bundle-Name"));
							
						}
						
						appendElement(be, "span").setTextContent(" - ");
						
						Element d = appendElement(be, "span");
						d.setTextContent(mf.getMainAttributes().getValue("bundle-description"));
						d.setAttribute("style", "font-style: italic");
						
//						appendElement(be, "bundle-category").setTextContent(attrs.getValue("Bundle-Category"));
//						addPackages(be, "import-package", attrs.getValue("Import-Package"));
//						addPackages(be, "export-package", attrs.getValue("Export-Package"));
						
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
