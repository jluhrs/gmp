package edu.gemini.aspen.gds.fits.checker


import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import java.io.File
import edu.gemini.aspen.gds.fits.FitsReader
import edu.gemini.aspen.gds.api.{GDSConfiguration, KeywordSource}
import edu.gemini.aspen.giapi.data.{ObservationEvent, ObservationEventHandler, DataLabel}
import org.apache.felix.ipojo.annotations.{Instantiate, Requires, Provides, Component}
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import scala.actors.Actor._
import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import java.util.logging.Logger

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationEventHandler]))
class IfsKeywordsChecker(@Requires configService: GDSConfigurationService,
                         @Requires obsState: ObservationStateRegistrar,
                         @Requires propertyHolder: PropertyHolder) extends ObservationEventHandler {
  protected val LOG = Logger.getLogger(this.getClass.getName)

  override def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    event match {
      case ObservationEvent.OBS_END_DSET_WRITE => actor {
        checkMissing(dataLabel, configService.getConfiguration)
      } //todo: use a service to get the directory
      case _ =>
    }
  }

  private[checker] def checkMissing(label: DataLabel, config: List[GDSConfiguration]) {
    val file = new File(propertyHolder.getProperty("DHS_SCIENCE_DATA_PATH"), label.toString)
    
    LOG.info("Verifying original keywords of " + file)
    val readerOpt: Option[FitsReader] = try {
      Some(new FitsReader(file))
    } catch {
      case _ => None
    }

    val configKeywords: Map[Int, Set[GDSConfiguration]] = config.filter{
      _.subsystem.name == KeywordSource.IFS
    }.toSet.groupBy(_.index.index)

    configKeywords foreach {
      case (index: Int, value: Set[_]) => {
        val keysInFile = readerOpt flatten {
          r => r.keys(index)
        }
        val keysInConfig = value map {
          case c:GDSConfiguration => c.keyword
        }
        val missingKeywords:Traversable[FitsKeyword] = keysInConfig &~ keysInFile.toSet

        if (missingKeywords.nonEmpty) {
          obsState.registerMissingKeyword(label, missingKeywords)
        }
      }
    }
  }

}