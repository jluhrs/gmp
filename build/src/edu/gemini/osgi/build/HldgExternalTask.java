//
// $Id$
//

package edu.gemini.osgi.build;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.unix.Symlink;

import java.io.File;

/**
 * A task used to create a symlink to an hldg external jar file.  Requires
 * a property to be defined (sorry):
 *
 * <ul>
 * <li>hldg.external.lib - must point to the external java lib area</li>
 * </ul>
 *
 * Requires a "jar" attribute which must be the path, relative to the
 * hldg.external.lib, that identifies the jar file.
 */
public class HldgExternalTask extends Task {

    private String jar;

    public void setJar(String jar) {
        this.jar = jar;
    }

    public void execute() throws BuildException {
        if (jar == null) {
            throw new BuildException("jar attribute not specified");
        }

        String externalLibDirStr = getProject().getProperty("hldg.external.lib");
        if (externalLibDirStr == null) {
            throw new BuildException("hldg.external.lib property missing");
        }

        File libDir = new File(getProject().getBaseDir(), "lib");

        String baseJar = getBaseName(jar);
        File link = new File(libDir, baseJar);
        link.delete();

        Symlink ln = new Symlink();
        ln.setProject(getProject());
        ln.setTaskName("hldgexternal");
        ln.setAction("single");
        ln.setLink(link.getPath());
        ln.setResource(externalLibDirStr + "/" + jar);
        ln.execute();
    }

    private static String getBaseName(String jar) {
        int i = jar.lastIndexOf("/");
        if (i == -1) return jar;
        return jar.substring(i+1);
    }
}
