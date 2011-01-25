#!/bin/sh
pax-run.sh --keepOriginalUrls --repositories=http://build.cl.gemini.edu:8081/artifactory/repo1,http://build.cl.gemini.edu:8081/artifactory/ext-releases-local scan-composite:file:$PWD/src/main/etc/conf/module.properties
