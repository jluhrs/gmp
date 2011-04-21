package edu.gemini.aspen.gds.keywordssets

import org.scalatest.junit.{JUnitSuite, ShouldMatchersForJUnit}
import org.junit.Test
import edu.gemini.aspen.giapi.data.Dataset

class KeywordSetComposerTest extends JUnitSuite with ShouldMatchersForJUnit {
    @Test
    def verifyInitMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataset = new Dataset("GS-2010A")
        val init = Init(dataset)
        init should have (
            'dataSet (dataset)
        )
    }

    @Test
    def verifyCompleteMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataset = new Dataset("GS-2010A")
        val complete = Complete(dataset)
        complete should have (
            'dataSet (dataset)
        )
    }

    @Test
    def verifyInitCompleteMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataset = new Dataset("GS-2010A")
        val complete = InitCompleted(dataset)
        complete should have (
            'dataSet (dataset)
        )
    }
}