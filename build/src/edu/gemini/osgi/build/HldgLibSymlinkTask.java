//
// $Id$
//

package edu.gemini.osgi.build;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.unix.Symlink;

import java.io.File;

/**
 * A task used to create a symlink to a jar file.  Puts the link in the
 * "lib" dir of the project and names it the same as the specified resource.
 * The ant "symlink" task almost does the same thing but doesn't allow you
 * to replace existing links and deleting the file deletes the file that you're
 * linked to.
 */
public class HldgLibSymlinkTask extends Task {

    private String res;

    public void setResource(String res) {
        this.res = res;
    }

    public void execute() throws BuildException {
        if (res == null) {
            throw new BuildException("resource attribute not specified");
        }

        File libDir = new File(getProject().getBaseDir(), "lib");

        String baseJar = getBaseName(res);
        File link = new File(libDir, baseJar);
        link.delete();

        Symlink ln = new Symlink();
        ln.setProject(getProject());
        ln.setTaskName("liblink");
        ln.setAction("single");
        ln.setLink(link.getPath());
        ln.setResource(res);
        ln.execute();
    }

    private static String getBaseName(String jar) {
        int i = jar.lastIndexOf("/");
        if (i == -1) return jar;
        return jar.substring(i+1);
    }
}
