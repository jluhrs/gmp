package edu.gemini.osgi.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.SubAnt;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.jar.JarFile;

public class BundleTask extends Task {

	private String subTarget;

	public void setTarget(String target) {
		subTarget = target;
	}

	@Override
	public void execute() throws BuildException {
		try {

			if (subTarget == null) {
				throw new BuildException();
			}

			final Project antProject = getProject();

			Builder b = new Builder() {
				int n = 1;
				protected void build(BundleProject p, String msg) {

					FileSet files = new FileSet();
					files.setFile(new File(p.getRoot(), "build.xml"));

					Path buildPath = new Path(getProject());
					buildPath.addFileset(files);

					SubAnt sub = new SubAnt();
					sub.setTarget(subTarget);
					sub.setProject(getProject());
					sub.setBuildpath(buildPath);
					sub.setFailonerror(true);

					System.out.println("\n*** Building bundle " + (n++) + ": " + p + " (" + msg + ")");
					sub.execute();
				}
			};

			File libdir = new File(antProject.getProperty("osgi.runtime.lib"));
			for (File f: libdir.listFiles()) {
				if (f.getName().endsWith(".jar"))
					b.addLibrary(new JarFile(f));
			}

			File bundledir = new File(antProject.getProperty("osgi.bundle"), "external");
			for (File f: bundledir.listFiles()) {
				if (f.getName().endsWith(".jar"))
					b.addBundle(new JarFile(f));
			}

            BundleProject spdbActivator = null;

            Queue<File> queue = new LinkedList<File>();
			queue.add(antProject.getBaseDir());
			while (!queue.isEmpty()) {
				File dir = queue.remove();
				if (BundleProject.isBundleProject(dir)) {
                    BundleProject bp = new BundleProject(dir);
                    if ("SPDB Activator".equals(bp.getName())) {
                        spdbActivator = bp;
                    } else {
                        b.addBundleProject(new BundleProject(dir));
                    }
                } else {
					for (File f: dir.listFiles()) {
						if (f.isDirectory()) queue.add(f);
					}
				}
			}


			System.out.println("Compiling " + b.size() + " bundles...");
			b.build();
			System.out.println("Done with bundle compile.");

            if (spdbActivator != null) {
                System.out.println("Compiling SPDB Activator ...");
                b.addBundleProject(spdbActivator);
                b.build();
                System.out.println("Done with activator.");
            }

        } catch (IOException ioe) {
			throw new BuildException(ioe);
		}

	}



}
