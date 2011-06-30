package edu.gemini.aspen.gds.actors.factory

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.gds.actors.configuration.GDSConfigurationParser
import edu.gemini.aspen.gds.api.{KeywordValueActor, KeywordActorsFactory, GDSConfiguration}
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

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
class CompositeActorsFactoryImpl(@Property(name = "keywordsConfiguration", value = "NOVALID", mandatory = true) configurationFile: String) extends CompositeActorsFactory {
    val LOG = Logger.getLogger(this.getClass.getName)

    var factories: List[KeywordActorsFactory] = List()

    override def configure(configuration: List[GDSConfiguration]) {
        factories foreach {
            _.configure(configuration)
        }
    }

    override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel): List[KeywordValueActor] = {
        factories flatMap {
            _.buildActors(obsEvent, dataLabel)
        }
    }

    /**
     * Method called when a new KeywordActorsFactory is registered
     */
    @Bind(aggregate = true, optional = true)
    def bindKeywordFactory(keywordFactory: KeywordActorsFactory) {
        keywordFactory.configure(actorsConfiguration)
        factories = keywordFactory :: factories
    }

    /**
     * Method called when a KeywordActorsFactory is unregistered
     */
    @Unbind(aggregate = true)
    def unbindKeywordFactory(keywordFactory: KeywordActorsFactory) {
        factories = factories filterNot {
            _ == keywordFactory
        }
    }

    /**
     * Method called when the Component is ready to start
     */
    @Validate
    def startConfiguration() {
        LOG.info("CompositeActorsFactory validated with config:" + configurationFile)
        val configurationList = new GDSConfigurationParser().parseFile(configurationFile)

        actorsConfiguration = configurationList filter {
            _.isInstanceOf[GDSConfiguration]
        } map {
            _.asInstanceOf[GDSConfiguration]
        }
        configure(actorsConfiguration)
    }
}