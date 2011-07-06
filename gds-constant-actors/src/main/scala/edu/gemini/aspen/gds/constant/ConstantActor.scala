package edu.gemini.aspen.gds.constant

import edu.gemini.aspen.gds.api._

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class ConstantActor(configurations: List[GDSConfiguration]) extends KeywordValueActor {
    override def collectValues(): List[CollectedValue[_]] = {
        for {config <- configurations} yield CollectedValue(config.keyword, config.nullValue.value, config.fitsComment.value, config.index.index)
    }

}
