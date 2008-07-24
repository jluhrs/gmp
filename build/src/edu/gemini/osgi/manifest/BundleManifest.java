//
// $Id$
//

package edu.gemini.osgi.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

final class BundleManifest {
    private File file;
    private Map<String, String> attrs;

    BundleManifest(File file) {
        this.file  = file;
        this.attrs = readManifest(file);
        defineDefaultAttributeValue("Manifest-Version", "1.0");
        defineDefaultAttributeValue("Bundle-Vendor", "Gemini 8m HLDG");
        defineDefaultAttributeValue("Bundle-Version", "1.0");
    }

    void setDefaultName(String name) {
        defineDefaultAttributeValue("Bundle-Name", name);
        defineDefaultAttributeValue("Bundle-Description", name);
    }

    void setDefaultClasspath(String classpath) {
        defineDefaultAttributeValue("Bundle-Classpath", classpath);
    }

    void setImports(String imports) {
        attrs.put("Import-Package", imports);
    }

    void setExports(String exports) {
        attrs.put("Export-Package", exports);
    }


    void write() {
        FileWriter w = null;
        try {
            w = new FileWriter(file);

            List<String> names = new ArrayList<String>(attrs.keySet());
            Collections.sort(names, new Comparator<Object>() {
                public int compare(Object o1, Object o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            for (String name : names) {
                String line = String.format("%s: %s\n", name, attrs.get(name));
                w.write(line);
            }
            w.flush();

        } catch (IOException ex) {
            throw new RuntimeException("Could not write manifest: " + file, ex);
        } finally {
            if (w != null) try { w.close(); } catch (Exception ex) {/* empty */}
        }

    }

    private void defineDefaultAttributeValue(String attrName, String defValue) {
        String val = attrs.get(attrName);
        if (val == null) attrs.put(attrName, defValue);
    }

    private static Map<String, String> readManifest(File f) {
        Map<String, String> map = new HashMap<String, String>();
        if (!f.exists()) return map;

        Manifest man;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            man = new Manifest(fis);
        } catch (IOException ex) {
            throw new RuntimeException("Could not open manifest file: " + f, ex);
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (Exception ex) {/*empty*/}
            }
        }

        Attributes attrs = man.getMainAttributes();
        for (Map.Entry<Object, Object> entry : attrs.entrySet()) {
            map.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return map;
    }

    // The manifest write method will fuck up the manifest with ^M on the line
    // ending and merging of multiple lines.

//    private static void writeManifest(Manifest man, File f) {
//        FileOutputStream fos = null;
//
//        try {
//            fos = new FileOutputStream(f);
//            man.write(fos);
//            fos.flush();
//        } catch (IOException ex) {
//            throw new BuildException("Could not write manifest file: " + f, ex);
//        } finally {
//            if (fos != null) {
//                try { fos.close(); } catch (Exception ex) { /*empty*/ }
//            }
//        }
//    }

}
