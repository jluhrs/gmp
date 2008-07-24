//
// $Id$
//

package edu.gemini.osgi.manifest;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates or updates a bundle's manifest. If the file manifest.mf
 * does not exist in the bundle's root directory, it is created with
 * initial attributes for Bundle-Classpath, Bundle-Description,
 * Bundle-Name, Bundle-Vender, Bundle-Version, Export-Package,
 * Import-Package, and Manifest-Version.
 *
 * <p>If the manifest.mf file does exist, then the Export-Package and
 * Import-Package are updated and the other properties are left
 * unmodified.
 *
 * <p>Assumes every package defined in Java classes in the src directory
 * of the bundle should be exported.  Assumes every non-standard Java
 * classes imported by Java classes in the src directory should be
 * imported by the bundle (except for those packages defined in jars
 * in the bundle's lib directory.
*/
public class ManifestMaker {
    private static final String[] STANDARD_IMPORT_ROOTS = {
            "com.sun.net",
            "java.",
            "javax.net",
            "javax.print",
            "javax.swing",
            "javax.xml",
            "junit.",
            "org.w3c",
            "org.xml.sax",
    };


    private File projectDir;
    private boolean exportJars;

    private ManifestMaker(File projectDir, boolean exportJars) {
        this.projectDir = projectDir;
        this.exportJars = exportJars;
    }

    public void execute() {
        File libDir = new File(projectDir, "lib");
        Set<String> libPackages = getLocalLibPackages(libDir);

        Set<String> exports = new HashSet<String>();
        Set<String> imports = new HashSet<String>();

        File srcDir = new File(projectDir, "src");
        if (srcDir.exists() && srcDir.isDirectory() && srcDir.canRead()) {
            scanDir(srcDir, exports, imports);
        }
        weedStandardImports(imports);
        imports.removeAll(exports);
        imports.removeAll(libPackages);

        if (exportJars) exports.addAll(libPackages);

        File manifestFile = new File(projectDir, "manifest.mf");
        BundleManifest man = new BundleManifest(manifestFile);
        man.setDefaultName(deriveBundleName());
        man.setDefaultClasspath(getClasspath(libDir));
        man.setImports(getSortedPackages(imports));
        man.setExports(getSortedPackages(exports));
        man.write();
    }

    private String getSortedPackages(Collection<String> packages) {
        List<String> sortedPackages = new ArrayList<String>(packages);
        Collections.sort(sortedPackages);
        if (sortedPackages.size() == 0) return "";

        StringBuilder buf = new StringBuilder();
        buf.append(sortedPackages.remove(0));

        for (String packageName : sortedPackages) {
            buf.append(",\n ").append(packageName);
        }
        return buf.toString();
    }

    private String getClasspath(File libDir) {
        // Set the class path based upon the lib directory contents.
        String path = ".";
        if (libDir.exists() && libDir.isDirectory()) {
            String[] libJars = libDir.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (libJars.length > 0) {
                Arrays.sort(libJars);
                StringBuilder buf = new StringBuilder();
                buf.append(path);
                for (String libJar : libJars) {
                    buf.append(", lib/").append(libJar);
                }
                path = buf.toString();
            }
        }

        return path;
    }

    private String deriveBundleName() {
        String name = projectDir.getName();
        name = name.replaceAll("-", " ");

        StringBuilder buf = new StringBuilder();
        for (StringTokenizer tok = new StringTokenizer(name); tok.hasMoreTokens(); ) {
            String tmp = tok.nextToken();
            buf.append(Character.toTitleCase(tmp.charAt(0)));
            buf.append(tmp.substring(1));
            buf.append(' ');
        }
        return buf.toString().trim();
    }


    private static void scanDir(File dir, Set<String> exports, Set<String> imports) {
        Collection<File> javaFiles = new ArrayList<File>();
        listJavaSources(dir, javaFiles);
        for (File f : javaFiles) {
            try {
                Deps d = parse(f);
                if (d.packageName != null) {
                    exports.add(d.packageName);
                }
                imports.addAll(d.imports);
            } catch (IOException e) {
                throw new RuntimeException("Problem reading file: " + f);
            }
        }
    }

    private static void listJavaSources(File dir, Collection<File> res) {
        File[] fileList = dir.listFiles();

        for (File f : fileList) {
            if (f.isFile() && f.getName().endsWith(".java")) {
                res.add(f);
            } else if (f.isDirectory()) {
                listJavaSources(f, res);
            }
        }
    }

    private static void weedStandardImports(Set<String> imports) {
        for (Iterator<String> it=imports.iterator(); it.hasNext(); ) {
            String packageName = it.next();
            for (String root : STANDARD_IMPORT_ROOTS) {
                if (packageName.startsWith(root)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private static Set<String> getLocalLibPackages(File libDir) {
        Set<String> packages = new HashSet<String>();
        if (!libDir.exists() || !libDir.isDirectory()) return packages;

        File[] jars = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        for (File jar : jars) {
            packages.addAll(getJarPackages(jar));
        }

        return packages;
    }

    private static Set<String> getJarPackages(File f) {
        Set<String> packages = new HashSet<String>();
        try {
            JarFile jarFile = new JarFile(f);
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();
                if (!name.endsWith(".class")) continue;

                int i = name.lastIndexOf("/");
                if (i <= 0) continue;
                packages.add(name.substring(0, i).replaceAll("/", "."));
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem reading jar file: " + f, e);
        }

        return packages;
    }

    private static class Deps {
        private static final Pattern PACKAGE_PATTERN = Pattern.compile("^package\\s*(.*);.*");
        private static final Pattern IMPORT_PATTERN = Pattern.compile("^import\\s*(.*);.*");

        String packageName;
        Set<String> imports = new HashSet<String>();

        void consider(String line) {
            if (packageName == null) {
                Matcher m = Deps.PACKAGE_PATTERN.matcher(line);
                if (m.matches()) {
                    packageName = m.group(1);
                    return;
                }
            }

            Matcher m = Deps.IMPORT_PATTERN.matcher(line);
            if (m.matches()) {
                String importStr = m.group(1);
                int i = importStr.lastIndexOf('.');
                if (i > 0) {
                    importStr = importStr.substring(0, i);
                }
                imports.add(importStr);
            }
        }
    }


    private static Deps parse(File javaSrc) throws IOException {
        FileReader fr = new FileReader(javaSrc);

        Deps d = new Deps();
        BufferedReader br = null;
        try {
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                d.consider(line);
            }
        } finally {
            if (br != null) br.close();
        }

        return d;
    }

    public static void main(String[] args) {

        boolean exportJars = false;
        File projectDir = new File(System.getProperty("user.dir"));
        for (String arg : args) {
            if ("-export".equals(arg)) {
                exportJars = true;
            } else {
                projectDir = new File(arg);
            }
        }

        ManifestMaker mm = new ManifestMaker(projectDir, exportJars);
        mm.execute();
    }
}
