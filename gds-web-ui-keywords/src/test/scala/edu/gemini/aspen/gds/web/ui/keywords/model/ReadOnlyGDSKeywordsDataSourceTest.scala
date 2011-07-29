package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.Conversions._

class ReadOnlyGDSKeywordsDataSourceTest {
  val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val config2 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY2", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")

  @Test
  def testWriteRead {
    val dataSource = new ReadOnlyGDSKeywordsDataSource(List(config1))
    assertEquals(List(config1), dataSource.toGDSConfiguration)
  }

  @Test
  def testGDSDisplayedFields {
    assertFalse(GDSKeywordsDataSource.displayedFields.isEmpty)
  }

  @Test
  def testWidth {
    val dataSource = new ReadOnlyGDSKeywordsDataSource(List(config1))
    assertEquals(30, dataSource.propertyWidth("Instrument"))
  }

  @Test
  def testAddItem {
    val dataSource = new ReadOnlyGDSKeywordsDataSource(List(config1))
    dataSource.addNewConfig(config2)
    // Cannot add a new config
    assertEquals(1, dataSource.toGDSConfiguration.size)
  }

  @Test
  def testDeleteItem {
    val dataSource = new ReadOnlyGDSKeywordsDataSource(List(config1))
    assertTrue(dataSource.removeItem(0))
    // Cannot remove an item
    assertEquals(1, dataSource.toGDSConfiguration.size)
  }

  @Test
  def testColumnHeader {
    val dataSource = new ReadOnlyGDSKeywordsDataSource(List(config1))
    assertEquals("Instrument", dataSource.propertyHeader("Instrument"))
  }

  @Test
  def testItemToGDSConfiguration {
    assertEquals(config1, GDSKeywordsDataSource.itemToGDSConfiguration(config1, Nil))

    val wrappers: List[GDSKeywordsDataSource.ConfigUpdateFunction] = List()
    assertEquals(config1, GDSKeywordsDataSource.itemToGDSConfiguration(config1, wrappers))
  }
}