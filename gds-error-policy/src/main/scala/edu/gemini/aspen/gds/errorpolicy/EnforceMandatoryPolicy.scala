package edu.gemini.aspen.gds.errorpolicy

import edu.gemini.aspen.giapi.data.DataLabel
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Provides, Component}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{Subsystem, KeywordSource, GDSConfiguration, DefaultCollectedValue, ErrorPolicy, CollectionError, ErrorCollectedValue, CollectedValue, DefaultErrorPolicy}

/**
 * This policy adds missing items(i.e. that are in the configuration but not among the CollectedValues) and then
 * transforms ErrorCollectedValues that have a CollectionError.MandatoryRequired cause, to a
 * CollectedValue with an empty string as a value, so that it gets written to the file.
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ErrorPolicy]))
class EnforceMandatoryPolicy(@Requires configService: GDSConfigurationService) extends DefaultErrorPolicy {
  override val priority = 2

  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    LOG.fine("Enforce mandatory keywords for " + dataLabel)

    headers ++ constructValuesForMissing(getMissing(headers)) map {
      case ErrorCollectedValue(keyword, CollectionError.MandatoryRequired, comment, index) => CollectedValue(keyword, "", comment, index)
      case c => c
    }
  }

  private def getMissing(headers: List[CollectedValue[_]]): List[GDSConfiguration] = {
    configService.getConfiguration filterNot {
      config => headers exists {
        collected => collected.keyword == config.keyword
      }
    } filter {
      config => config.subsystem != Subsystem(KeywordSource.IFS)
    }
  }

  private def constructValuesForMissing(configurations: List[GDSConfiguration]): List[CollectedValue[_]] = {
    val list = configurations map {
      case config => if (config.isMandatory) {
        CollectedValue(config.keyword, "", config.fitsComment.value, config.index.index)
      } else {
        new DefaultCollectedValue(config.keyword, config.nullValue.value, config.fitsComment.value, config.index.index)
      }
    }
    list.asInstanceOf[List[CollectedValue[_]]]
  }
}