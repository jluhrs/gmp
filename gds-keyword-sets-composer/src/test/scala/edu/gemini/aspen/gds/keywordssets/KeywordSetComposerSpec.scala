package edu.gemini.aspen.gds.keywordssets

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.{FeatureSpec, Spec}
import edu.gemini.aspen.giapi.data.Dataset

@RunWith(classOf[JUnitRunner])
class KeywordSetComposerSpec extends FeatureSpec {
    feature("Keyword Set should accept init messages") {
        scenario("init") {
            val dataSet = new Dataset("GS-2011")

            val composer = KeywordSetComposer()
            composer.start

            composer ! Init(dataSet)
        }
    }
}