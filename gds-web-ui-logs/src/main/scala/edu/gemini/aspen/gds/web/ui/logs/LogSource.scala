package edu.gemini.aspen.gds.web.ui.logs

import org.ops4j.pax.logging.spi.PaxLoggingEvent

/**
 * Interface for the OSGiService */
trait LogSource {
  def logEvents:Iterable[PaxLoggingEvent]
}