package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import edu.gemini.aspen.gds.api.{CollectedValue, DefaultPostProcessingPolicy, GDSConfiguration}
import edu.gemini.aspen.giapi.data.DataLabel

/**
 * This policy ensures the CollectedValues are in the same order they appear in the config file.
 */
class EnforceOrderPolicy(configService: GDSConfigurationService) extends DefaultPostProcessingPolicy {
  override val priority = 8

  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    LOG.fine("Enforce order of keywords for " + dataLabel)

    val config = configService.getConfiguration

    val indexedConfig = addIndex(0, config).toMap

    headers.sortWith({
      (x, y) => indexedConfig.getOrElse(x.keyword, 0) <= indexedConfig.getOrElse(y.keyword, 0)
    })

  }

  private def addIndex(i: Int, list: List[GDSConfiguration]): List[Tuple2[FitsKeyword, Int]] = {
    if (list.nonEmpty) {
      (list.head.keyword, i) :: addIndex(i + 1, list.tail)
    } else {
      Nil
    }
  }

  override def toString: String = this.getClass.getSimpleName
}