package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{KeywordSource, ErrorCollectedValue, CollectedValue, ErrorPolicy}
import java.util.logging.Logger

/**
 * This policy checks for errors and missing values and adds them to the ObservationStateRegistrar.
 * It should be run after EnforceMandatory policy and before ErrorsRemoved policy applied.
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ErrorPolicy]))
class InspectPolicy(@Requires configService: GDSConfigurationService, @Requires obsState: ObservationStateRegistrar) extends ErrorPolicy {
  protected val LOG = Logger.getLogger(this.getClass.getName)
  override val priority = 5

  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    LOG.fine("Inspect headers looking for missing value")

    checkErrors(dataLabel, headers)
    //checkMissing(dataLabel, headers)
    headers
  }

  private def checkErrors(label: DataLabel, headers: List[CollectedValue[_]]) {
    obsState.registerCollectionError(label, headers collect {
      case c: ErrorCollectedValue => (c.keyword, c.error)
    })
  }

  private def checkMissing(label: DataLabel, headers: List[CollectedValue[_]]) {
    val configList = configService.getConfiguration filterNot {
      _.subsystem.name == KeywordSource.INSTRUMENT //IFS keywords are not written by us, they are already in the file
    }
    obsState.registerMissingKeyword(label, configList filterNot {
      config => headers exists {
        collected => collected.keyword == config.keyword
      }
    } collect {
      case config => config.keyword
    })
  }

  override def toString = this.getClass.getSimpleName
}