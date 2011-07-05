package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel

/**
 * Defines an error policy
 */
trait ErrorPolicy {
    /**
     * Lower priority policies are applied first
     */
    val priority = 5

    /**
     * Applies the policy taking a set of proposed headers and returning the set of headers allowed
     *
     * @param dataLabel The data label being processed
     * @param headers Proposed set of headers
     * @return A list of headers to be written to the fits file
     */
    def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]]
}

/**
 * The default error policy
 */
class DefaultErrorPolicy extends ErrorPolicy {
    // Let all the original headers to be applied
    override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = headers
}