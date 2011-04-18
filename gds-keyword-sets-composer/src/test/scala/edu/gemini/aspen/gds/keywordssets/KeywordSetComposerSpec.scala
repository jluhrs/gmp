package edu.gemini.aspen.gds.keywordssets

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FeatureSpec
import edu.gemini.aspen.giapi.data.Dataset
import org.scalatest.matchers.ShouldMatchers
import scala.actors.Actor
import Actor._

@RunWith(classOf[JUnitRunner])
class KeywordSetComposerSpec extends FeatureSpec with ShouldMatchers {
    feature("Keyword Set should process init messages") {
        scenario("init") {
            // Generate dataset
            val dataSet = new Dataset("GS-2011")

            val dummyActorsFactory = new DummyActorsFactory()
            // Create composer
            val composer = KeywordSetComposer(dummyActorsFactory)
            
            // Send an init message
            val result = composer !! Init(dataSet)

            result() match {
                case InitCompleted(replyDataSet) => replyDataSet should be (dataSet)
                case _ => fail("Should not reply other message")
            }
        }
    }
    feature("Keyword Set should accept complete messages") {
        scenario("init") (pending)
    }
}

class DummyActorsFactory extends KeywordActorsFactory {
    def startObservationActors(dataSet: Dataset): List[Actor] = {
        val dummyActor = actor {
            react {
                case Collect => reply("collected")
            }
        }
        dummyActor :: Nil
    }
}