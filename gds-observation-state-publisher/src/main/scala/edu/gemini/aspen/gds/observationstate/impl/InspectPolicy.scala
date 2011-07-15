package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{KeywordSource, ErrorCollectedValue, CollectedValue, ErrorPolicy}

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
        obsState.registerCollectionError(label, headers collect {
            case collected: ErrorCollectedValue => collected
        } collect {
            case c => (c.keyword, c.error)
        })
    }

    private def checkMissing(label: DataLabel, headers: List[CollectedValue[_]]) {
        val configList = configService.getConfiguration filterNot {
            _.subsystem.name == KeywordSource.IFS //IFS keywords are not written by us, they are already in the file
        }
        obsState.registerMissingKeyword(label, configList filterNot {
            config => headers exists {
                collected => collected.keyword == config.keyword
            }
        } collect {
            case config => config.keyword
        })
    }
}