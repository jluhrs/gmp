package edu.gemini.osgi.build;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public abstract class Builder {

	private final Set<String> packages = new TreeSet<String>();
	private final Set<BundleProject> projects = new HashSet<BundleProject>();
	
	public synchronized void addLibrary(JarFile jar) throws IOException {
		Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry je = e.nextElement();
			String en = je.getName();
			if (!je.isDirectory() && en.endsWith(".class")) {				
				int pos = en.lastIndexOf('/');
				if (pos != -1) {
					String pn = en.substring(0, pos).replace('/', '.');				
					packages.add(pn);
				}
			}
		}
	}

	public synchronized void addBundle(JarFile jar) throws IOException {
		Bundle b = new Bundle(jar);
		packages.addAll(b.getExports());
//		System.out.println("added pre-built bundle: " + b);
	}

	
	public synchronized void addBundleProject(BundleProject project) {
		projects.add(project);
	}
	
	public synchronized void build() {

		for (boolean go = true; go; ) {

			go = false;
			
			// First build all the projects that have completely resolved references.		
			for (boolean makingProgress = true; makingProgress; ) {
				makingProgress = false;
				for (Iterator<BundleProject> it = projects.iterator(); it.hasNext(); ) {
					BundleProject p = it.next();
					if (packages.containsAll(p.getImports())) {
						it.remove();
						build(p, "100% Resolved: " + p.getImports() + "; exporting " + p.getExports());
						packages.addAll(p.getExports());
						makingProgress = true;
					}
				}
			}
	
			// Now collect a list of exports that have not yet been made.
			Set<String> future = new TreeSet<String>();
			for (BundleProject p: projects) {
				future.addAll(p.getExports());
			}
			
			// If we can find a project that has only external references, try
			// to build it
			for (Iterator<BundleProject> it = projects.iterator(); it.hasNext(); ) {
				BundleProject p = it.next();
				Set<String> imports = new TreeSet<String>(p.getImports());
				imports.retainAll(future);
				if (imports.isEmpty()) {
					it.remove();

					Set<String> unresolved = new TreeSet<String>(p.getImports());
					unresolved.removeAll(future);
					Set<String> resolved = new TreeSet<String>(p.getImports());
					resolved.removeAll(unresolved);

					build(p, "Resolved " + resolved + ", unresolved " + unresolved);
					packages.addAll(p.getExports());
					go = true;
					break;
				}
			}
			
			
		
		}
		
		for (BundleProject p: projects) {
			TreeSet<String> unresolved = new TreeSet<String>(p.getImports());
			unresolved.removeAll(packages);
			System.out.println("Not built: " + p + " - could not find " + unresolved);
		}
		
		if (projects.size() > 0)
			throw new RuntimeException("Couldn't finish the build, sorry.");
		
	}

	protected abstract void build(BundleProject p, String msg);

	public int size() {
		return projects.size();
	}
		
}

