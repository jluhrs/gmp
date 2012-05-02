package edu.gemini.aspen.gds.errorpolicy

import edu.gemini.aspen.giapi.data.DataLabel
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Provides, Component}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{GDSConfiguration, ErrorPolicy, CollectedValue, DefaultErrorPolicy}
import edu.gemini.aspen.gds.api.fits.FitsKeyword

/**
 * This policy ensures the CollectedValues are in the same order they appear in the config file.
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ErrorPolicy]))
class EnforceOrderPolicy(@Requires configService: GDSConfigurationService) extends DefaultErrorPolicy {
  override val priority = 10

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

  override def toString = this.getClass.getSimpleName
}