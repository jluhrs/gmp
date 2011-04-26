package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.giapi.data.Dataset
import actors.Actor
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywordssets.KeywordActorsFactory
import xml.{Elem, XML}
import java.util.logging.Logger

trait StartAcquisitionActorsFactory extends KeywordActorsFactory

@Component
@Provides(specifications = Array(classOf[StartAcquisitionActorsFactory]))
class ObservationStartActorsFactory(@Property(name="startObservationFactory", value="INVALID", mandatory = true) fileName: String) extends StartAcquisitionActorsFactory {
    val LOG = Logger.getLogger(classOf[ObservationStartActorsFactory].getName)

    var factories:List[KeywordActorsFactory] = List()

    /**
     * Composite of the other factories registered as OSGI services
     */
    override def startObservationActors(dataSet: Dataset): List[Actor] = {
        factories flatMap (
            _.startObservationActors(dataSet)
        )
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