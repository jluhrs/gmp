package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.aspen.gds.performancemonitoring.EventLogger
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}
import edu.gemini.aspen.gds.api.fits.Header
import edu.gemini.aspen.gds.api.CollectedValue
import com.google.common.base.Stopwatch
import edu.gemini.aspen.gds.fits.FitsUpdater
import java.util.logging.{Level, Logger}
import java.io.File

/**
 * Utility class capable of coordinating the process of writing the FITS file once all observations have arrived */
class FitsFileProcessor(propertyHolder: PropertyHolder, eventLogger: EventLogger[DataLabel, ObservationEvent])(implicit LOG: Logger) {
  /**
   * Converts a list of collected value to a list of headers containing the values */
  def convertToHeaders(processedList: List[CollectedValue[_]]): Seq[Header] = {
    val maxHeader = (0 /: processedList)((i, m) => m.index.max(i))

    0 to maxHeader map {
      headerIndex => {
        val headerItems = processedList filter {
          _.index == headerIndex
        } map {
          _ match {
            // Implicit conversion
            case c => c._type.collectedValueToHeaderItem(c)
          }
        }
        Header(headerIndex, headerItems)
      }
    }
  }

  /**
   * Coreographs the process of writing and update fits file sending the required information for book keeping */
  def updateFITSFile(dataLabel: DataLabel, processedList: List[CollectedValue[_]]): Either[String, String] = {
    val headers = convertToHeaders(processedList)

    val stopwatch = new Stopwatch().start()
    val srcPath = propertyHolder.getProperty("DHS_SCIENCE_DATA_PATH")
    val destPath = propertyHolder.getProperty("DHS_PERMANENT_SCIENCE_DATA_PATH")

    try {
      val fu = new FitsUpdater(new File(srcPath), new File(destPath), dataLabel, headers.toList)
      fu.updateFitsHeaders()
      Right("Writing updated FITS file at " + dataLabel.toString + " took " + stopwatch.stop().elapsedMillis() + " [ms]")
    } catch {
      case ex =>
        LOG.log(Level.SEVERE, ex.getMessage, ex)
        Left(ex.getMessage)
    }

  }
}