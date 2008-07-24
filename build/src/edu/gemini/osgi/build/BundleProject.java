package edu.gemini.osgi.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

public class BundleProject extends Bundle {

	private final File root;
	
	public BundleProject(File root) throws IOException {
		super(new Manifest(new FileInputStream(new File(root, "manifest.mf"))));
		this.root = root;
	}

	public static boolean isBundleProject(File dir) {
		return new File(dir, "manifest.mf").exists();
	}

	public File getRoot() {
		return root;
	}
	
}
