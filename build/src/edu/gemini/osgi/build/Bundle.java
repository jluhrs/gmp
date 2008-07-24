package edu.gemini.osgi.build;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Bundle {

	private final Manifest manifest;
	private final String name;
	private final Set<String> imports = new TreeSet<String>();
	private final Set<String> exports = new TreeSet<String>();

	public Bundle(Manifest manifest) {
		this.manifest = manifest;
		Attributes attrs = manifest.getMainAttributes();
		name = attrs.getValue("Bundle-Name");
		for (String s: packages(attrs.getValue("Import-Package"))) imports.add(s);
		for (String s: packages(attrs.getValue("Export-Package"))) exports.add(s);
	}

	public Bundle(JarFile jar) throws IOException {
		this(jar.getManifest());
	}

	public static boolean isProject(File dir) {
		return new File(dir, "manifest.mf").exists();
	}

	public Set<String> getExports() {
		return Collections.unmodifiableSet(exports);
	}

	public Set<String> getImports() {
		return Collections.unmodifiableSet(imports);
	}

	public Manifest getManifest() {
		return manifest;
	}

    public String getName() {
        return name;
    }

    @Override
	public String toString() {
		return name;
	}

	private String[] packages(String attr) {
		if (attr == null) attr = "";
		String[] ret = attr.split("\\s*,\\s*");
		for (int i = 0; i < ret.length; i++) {
			int pos = ret[i].indexOf(";");
			if (pos != -1) ret[i] = ret[i].substring(0, pos - 1);
		}
		return ret;
	}

}
