package edu.gemini.aspen.gds.keywordssets

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import edu.gemini.aspen.giapi.data.DataLabel
import org.scalatest.matchers.ShouldMatchers
import scala.actors.Actor
import Actor._
import org.scalatest.Spec
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabaseImpl

@RunWith(classOf[JUnitRunner])
class KeywordSetComposerSpec extends Spec with ShouldMatchers {
    describe("A KeywordSetComposer") {
        it("should reply to StartAcquisition messages") {
            val (dataLabel, composer) = createFixture

            // Send an init message
            val result = composer !! StartAcquisition(dataLabel)

            result() match {
                case StartAcquisitionReply(replyDataSet) => replyDataSet should be (dataLabel)
                case _ => fail("Should not reply other message")
            }
        }
        it("should reply to EndAcquisition messages") {
            val (dataLabel, composer) = createFixture

            // Send an init message
            val result = composer !! EndAcquisition(dataLabel)

            result() match {
                case EndAcquisitionReply(replyDataSet) => replyDataSet should be (dataLabel)
                case _ => fail("Should not reply other message")
            }
        }
    }

    private def createFixture = {
        // Generate dataset
        val dataLabel = new DataLabel("GS-2011")

        val dummyActorsFactory = new DummyActorsFactory()
        val keywordsDatabase = new KeywordsDatabaseImpl()
        // Create composer
        val composer = KeywordSetComposer(dummyActorsFactory, keywordsDatabase)
        (dataLabel, composer)
    }
}

