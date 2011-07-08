package edu.gemini.aspen.gds.config

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationParser


class GDSConfigValidator extends GDSConfigurationParser {

}

//opciones: -h, -f archivo, -p (print)
object GDSConfigValidator {
    def main(args: Array[String]) {
        val parser = new GDSConfigValidator()
        if (!args.isEmpty) {
            parser.parseFile(args(0))
        } else {
            val result = parser.parseFile("/Users/nbarriga/Development/giapi-osgi-trunk/gmp-server/src/main/etc/conf/gds-keywords.conf")
            for (res <- result) {
                println(res)
            }
        }
    }
}