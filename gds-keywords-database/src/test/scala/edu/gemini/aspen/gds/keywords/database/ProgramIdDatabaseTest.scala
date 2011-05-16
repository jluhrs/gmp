package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.gds.api.Conversions._

class ProgramIdDatabaseTest extends AssertionsForJUnit {
  @Test
  def testBasic() {
    val db = new ProgramIdDatabaseImpl
    db ! StoreProgramId("label","id")

    val result = (db !? RetrieveProgramId("label")).asInstanceOf[Option[String]]

    assert(result.isDefined)
    assert(result.get == "id")
  }
}