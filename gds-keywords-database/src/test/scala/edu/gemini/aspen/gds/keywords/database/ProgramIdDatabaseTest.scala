package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.gds.api.Conversions._
import scala.Some

class ProgramIdDatabaseTest extends AssertionsForJUnit {
  @Test
  def testBasic() {
    val db = new ProgramIdDatabaseImpl
    db ! StoreProgramId("label", "id")

    (db !? (1000, RetrieveProgramId("label"))) match {
      case Some(Some("id")) =>
      case _ => fail()
    }


  }
}