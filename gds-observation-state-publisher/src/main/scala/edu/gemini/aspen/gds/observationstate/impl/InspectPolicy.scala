package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{ErrorCollectedValue, CollectedValue, ErrorPolicy}

@Component
@Instantiate
@Provides(specifications = Array(classOf[ErrorPolicy]))
class InspectPolicy(@Requires configService: GDSConfigurationService, @Requires obsState: ObservationStateRegistrar) extends ErrorPolicy {
    override val priority = 0

    override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
        checkErrors(dataLabel, headers)
        checkMissing(dataLabel, headers)
        headers
    }

    private def checkErrors(label: DataLabel, headers: List[CollectedValue[_]]) {
        headers collect {
            case collected: ErrorCollectedValue => collected
        } foreach {
            c => obsState.registerCollectionError(label, c.keyword, c.error)
        }
    }

    private def checkMissing(label: DataLabel, headers: List[CollectedValue[_]]) {
        val configList = configService.getConfiguration
        configList(0).keyword
        configList filterNot {
            config => headers exists {
                collected => collected.keyword == config.keyword
            }
        } foreach {
            config => obsState.registerMissingKeyword(label, config.keyword)
        }
    }
}