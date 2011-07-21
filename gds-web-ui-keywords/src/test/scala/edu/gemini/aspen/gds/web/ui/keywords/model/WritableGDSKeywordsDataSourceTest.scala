package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.Conversions._
import com.vaadin.data.Item

class WritableGDSKeywordsDataSourceTest {
  @Test
  def testWriteRead {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new WritableGDSKeywordsDataSource(List(config1))

    assertEquals(List(config1), dataSource.toGDSConfiguration)
  }

  @Test
  def testGDSDisplayedFields {
    assertFalse(GDSKeywordsDataSource.displayedFields.isEmpty)
  }

  @Test
  def testWidth {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new WritableGDSKeywordsDataSource(List(config1))
    assertEquals(30, dataSource.propertyWidth("Instrument"))
  }

  @Test
  def testDeleteItem {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new WritableGDSKeywordsDataSource(List(config1))
    assertTrue(dataSource.removeItem(0))
    assertTrue(dataSource.toGDSConfiguration.isEmpty)
  }

  @Test
  def testItemToGDSConfiguration {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")

    assertEquals(config1, GDSKeywordsDataSource.itemToGDSConfiguration(config1, Nil))

    val wrappers: List[GDSKeywordsDataSource.WrappedConfigItem] = List()
    assertEquals(config1, GDSKeywordsDataSource.itemToGDSConfiguration(config1, wrappers))
  }
}