package edu.gemini.aspen.gds.keywordssets

import org.scalatest.junit.{JUnitSuite, ShouldMatchersForJUnit}
import org.junit.Test
import edu.gemini.aspen.giapi.data.DataLabel

class KeywordSetComposerTest extends JUnitSuite with ShouldMatchersForJUnit {
    @Test
    def verifyInitMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataLabel = new DataLabel("GS-2010A")
        val init = Init(dataLabel)
        init should have (
            'dataLabel (dataLabel)
        )
    }

    @Test
    def verifyCompleteMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataLabel = new DataLabel("GS-2010A")
        val complete = Complete(dataLabel)
        complete should have (
            'dataLabel (dataLabel)
        )
    }

    @Test
    def verifyInitCompleteMessage() {
        // This is a contrived test as the semantics of case classes ensures the result already
        val dataLabel = new DataLabel("GS-2010A")
        val complete = InitCompleted(dataLabel)
        complete should have (
            'dataLabel (dataLabel)
        )
    }
}