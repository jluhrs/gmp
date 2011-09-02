package edu.gemini.aspen.gds.web.ui.logs

import model.LogEventWrapper

/**
 * Interface for the OSGiService */
trait LogSource {
  def logEvents:Iterable[LogEventWrapper]
}