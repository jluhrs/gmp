#!/bin/sh
pax-run.sh --keepOriginalUrls --vmo="-Dconf.base=../src/main/etc/conf -Dlogs.dir=logs -Xverify:none -Dorg.osgi.framework.system.packages.extra=sun.misc" --repositories=http://build.cl.gemini.edu:8081/artifactory/repo1,http://build.cl.gemini.edu:8081/artifactory/ext-releases-local scan-composite:file:$PWD/src/main/etc/conf/module.properties
