package edu.gemini.aspen.gds.actors.factory

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordActorsFactory, GDSConfiguration}
import scala.collection._

/**
 * Interface for a Composite of Actors Factory required by OSGi
 */
trait CompositeActorsFactory extends KeywordActorsFactory

/**
 * A composite Actors Factory that can listen for OSGi services registered as
 * keyword actors factories */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[CompositeActorsFactory]))
class CompositeActorsFactoryImpl(@Requires configService: GDSConfigurationService) extends AbstractKeywordActorsFactory with CompositeActorsFactory {
  // List of composed factories
  @volatile var factories = immutable.List[KeywordActorsFactory]()

  override def configure(configuration: immutable.List[GDSConfiguration]) {
    // Configure each factory in the composite
    factories foreach {
      _.configure(configuration)
    }
  }

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    // Fetch the latest configuration
    actorsConfiguration = configService.getConfiguration

    configure(actorsConfiguration)

    // Collects all actors built by each factory
    factories flatMap {
      _.buildActors(obsEvent, dataLabel)
    }
  }

  /**
   * Method called when a new KeywordActorsFactory is registered */
  @Bind(aggregate = true, optional = true)
  def bindKeywordFactory(keywordFactory: KeywordActorsFactory) {
    keywordFactory.configure(actorsConfiguration)
    factories = keywordFactory :: factories
  }

  /**
   * Method called when a KeywordActorsFactory is unregistered */
  @Unbind(aggregate = true)
  def unbindKeywordFactory(keywordFactory: KeywordActorsFactory) {
    factories = factories filterNot {
      _ == keywordFactory
    }
  }

  @Validate
  def startConfiguration() {
    // Required by iPojo
  }
}