package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel
import java.util.logging.Logger
import scala.collection._

/**
 * Defines an error policy
 */
trait ErrorPolicy {
  /**
   * Lower priority policies are applied first (0 - 10)
   */
  val priority = 5
  //todo: maybe change to an enum or Byte, to have an appropriate range of possible values.

  /**
   * Applies the policy taking a set of proposed headers and returning the set of headers allowed
   *
   * @param dataLabel The data label being processed
   * @param headers Proposed set of headers
   * @return A list of headers to be written to the fits file
   */
  def applyPolicy(dataLabel: DataLabel, headers: immutable.List[CollectedValue[_]]): immutable.List[CollectedValue[_]]
}

/**
 * The default error policy
 */
class DefaultErrorPolicy extends ErrorPolicy {
  protected val LOG = Logger.getLogger(this.getClass.getName)

  // Let all the original headers to be applied
  override def applyPolicy(dataLabel: DataLabel, headers: immutable.List[CollectedValue[_]]): immutable.List[CollectedValue[_]] = headers
}