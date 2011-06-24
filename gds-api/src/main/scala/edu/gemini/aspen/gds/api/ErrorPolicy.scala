package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.Header

/**
 * Defines an error policy
 */
trait ErrorPolicy {
    /**
     * Applies the policy taking a set of proposed headers and returning the set of headers allowed
     *
     * @param dataLabel The data label being processed
     * @param headers Proposed set of headers
     * @return An Optional list of headers to be written to the fits file
     */
    def applyPolicy(dataLabel:DataLabel, headers: Option[List[CollectedValue[_]]]): Option[List[CollectedValue[_]]]
}

/**
 * The default error policy
 */
class DefaultErrorPolicy extends ErrorPolicy {
    // Let all the original headers to be applied
    override def applyPolicy(dataLabel:DataLabel, headers: Option[List[CollectedValue[_]]]): Option[List[CollectedValue[_]]] = headers
}