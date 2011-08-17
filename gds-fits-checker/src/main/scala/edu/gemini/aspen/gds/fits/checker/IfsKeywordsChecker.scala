package edu.gemini.aspen.gds.fits.checker


import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import java.io.File
import edu.gemini.aspen.gds.fits.FitsReader
import edu.gemini.aspen.gds.api.{GDSConfiguration, KeywordSource}
import edu.gemini.aspen.giapi.data.{ObservationEvent, ObservationEventHandler, DataLabel}
import org.apache.felix.ipojo.annotations.{Instantiate, Requires, Provides, Component}
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import scala.actors.Actor._

@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class IfsKeywordsChecker(@Requires configService: GDSConfigurationService, @Requires obsState: ObservationStateRegistrar) extends ObservationEventHandler {

  override def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    event match {
      case ObservationEvent.OBS_END_DSET_WRITE => actor {
        checkMissing(dataLabel, new File("/tmp/" + dataLabel + ".fits"), configService.getConfiguration)
      } //todo: use a service to get the directory
      case _ =>
    }
  }

  private[checker] def checkMissing(label: DataLabel, file: File, config: List[GDSConfiguration]) {
    val readerOpt: Option[FitsReader] = try {
      Some(new FitsReader(file))
    }
    catch {
      case _ => None
    }

    val configKeywords: Map[Int, Set[GDSConfiguration]] = config.filter({
      _.subsystem.name == KeywordSource.IFS
    }).toSet.groupBy(_.index.index)

    configKeywords foreach {
      case (key: Int, value: Set[GDSConfiguration]) => {
        val missingKeywords = (value map {
          _.keyword
        }) &~ readerOpt.collect({
          case x: FitsReader => x.getKeywords(key)
        }).getOrElse(Set.empty)
        if (missingKeywords.nonEmpty) {
          obsState.registerMissingKeyword(label, missingKeywords)
        }
      }
    }
  }

}