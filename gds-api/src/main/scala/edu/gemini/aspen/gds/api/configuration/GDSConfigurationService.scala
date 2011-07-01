package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations.{Property, Provides, Component}

trait GDSConfigurationService {
    def getConfiguration: List[GDSConfiguration]
}

@Component
@Provides(specifications = Array(classOf[GDSConfigurationService]))
class GDSConfigurationServiceImpl(@Property(name = "keywordsConfiguration", value = "NOVALID", mandatory = true) configurationFile: String) extends GDSConfigurationService {
    def getConfiguration: List[GDSConfiguration] = {
        new GDSConfigurationParser().parseFile(configurationFile) filter {
            _.isInstanceOf[GDSConfiguration]
        } map {
            _.asInstanceOf[GDSConfiguration]
        }
    }
}