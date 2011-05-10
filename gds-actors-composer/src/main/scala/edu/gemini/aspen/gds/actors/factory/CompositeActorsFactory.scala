package edu.gemini.aspen.gds.actors.factory

import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.gds.api.{KeywordActorsFactory, GDSConfiguration}
import edu.gemini.aspen.gds.actors.configuration.GDSConfigurationParser

/**
 * Interface for a Composite of Actors Factory required by OSGi
 */
trait CompositeActorsFactory extends KeywordActorsFactory

/**
 * A composite Actors Factory that can listen for OSGi services registered as
 * keyword actors factories
 */
@Component
@Provides(specifications = Array(classOf[CompositeActorsFactory]))
class CompositeActorsFactoryImpl(@Property(name="keywordsConfiguration", value = "NOVALID", mandatory = true) configurationFile:String) extends CompositeActorsFactory {
    val LOG = Logger.getLogger(classOf[CompositeActorsFactory].getName)

    var factories:List[KeywordActorsFactory] = List()
    var config:List[GDSConfiguration] = List()

    /**
     * Composite of the other factories registered as OSGI services
     */
    override def startAcquisitionActors(dataLabel: DataLabel): List[Actor] = {
        factories flatMap (
            _.startAcquisitionActors(dataLabel)
        )
    }

    override def endAcquisitionActors(dataLabel: DataLabel): List[Actor] = {
        factories flatMap (
            _.endAcquisitionActors(dataLabel)
        )
    }

    override def configure(configuration:List[GDSConfiguration]) {
        factories foreach {
            _.configure(configuration)
        }
    }

    @Bind(aggregate = true, optional = true)
    def bindKeywordFactory(keywordFactory:KeywordActorsFactory) {
        keywordFactory.configure(config)
        factories = keywordFactory :: factories
    }

    @Unbind(aggregate = true)
    def unbindKeywordFactory(keywordFactory:KeywordActorsFactory) {
        keywordFactory.configure(config)
        factories = factories filterNot (_ == keywordFactory)
    }

    @Validate()
    def validate() {
        LOG.info("ObservationStartFactory validated with config:"  + configurationFile)
        val configurationList = new GDSConfigurationParser().parseFile(configurationFile)

        config = for (x <- configurationList if x.isInstanceOf[GDSConfiguration] ) yield {
            x.asInstanceOf[GDSConfiguration]
        }
        configure(config)
    }
}