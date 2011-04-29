package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.gds.actors.KeywordActorsFactory
import edu.gemini.aspen.gds.keywordssets.configuration.GDSConfiguration

/**
 * Interface for a Composite of Actors Factory required by OSGi
 */
trait CompositeActorsFactory extends KeywordActorsFactory

/**
 * A composite Actors Factory that can listen for OSGi services registered as
 * keyword actors factories
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[CompositeActorsFactory]))
class CompositeActorsFactoryImpl extends CompositeActorsFactory {
    val LOG = Logger.getLogger(classOf[CompositeActorsFactory].getName)

    var factories:List[KeywordActorsFactory] = List()

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
        factories = keywordFactory :: factories
    }

    @Unbind(aggregate = true)
    def unbindKeywordFactory(keywordFactory:KeywordActorsFactory) {
        factories = factories filterNot (_ == keywordFactory)
    }

    @Validate()
    def validate() {LOG.info("ObservationStartFactory validated" )}
}