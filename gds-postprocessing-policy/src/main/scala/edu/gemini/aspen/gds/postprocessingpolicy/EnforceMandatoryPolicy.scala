package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.DataLabel

/**
 * This policy adds missing items(i.e. that are in the configuration but not among the CollectedValues) and then
 * transforms ErrorCollectedValues that have a CollectionError.MandatoryRequired cause, to a
 * CollectedValue with an empty string as a value, so that it gets written to the file.
 */
class EnforceMandatoryPolicy(configService: GDSConfigurationService) extends DefaultPostProcessingPolicy {
  override val priority = 2

  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    LOG.fine("Enforce mandatory keywords for " + dataLabel)

    headers ++ constructValuesForMissing(getMissing(headers))
  }

  private def getMissing(headers: List[CollectedValue[_]]): List[GDSConfiguration] = {
    configService.getConfiguration filterNot {
      config => headers exists {
        collected => collected.keyword == config.keyword
      }
    } filter {
      config => config.subsystem != Subsystem(KeywordSource.INSTRUMENT)
    }
  }

  private def constructValuesForMissing(configurations: List[GDSConfiguration]): List[CollectedValue[_]] = configurations collect {
    case config if !config.isMandatory => new ErrorCollectedValue(config.keyword, CollectionError.ItemNotFound, config.fitsComment.value, config.index.index)
    case config if config.isMandatory => new ErrorCollectedValue(config.keyword, CollectionError.MandatoryRequired, config.fitsComment.value, config.index.index)
  }

  override def toString: String = this.getClass.getSimpleName
}