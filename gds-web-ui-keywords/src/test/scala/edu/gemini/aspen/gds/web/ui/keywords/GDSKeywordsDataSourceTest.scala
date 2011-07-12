package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.web.ui.keywords.GDSKeywordsDataSource._
import edu.gemini.aspen.gds.api.Conversions._
import com.vaadin.data.Item

class GDSKeywordsDataSourceTest {
  @Test
  def testWriteRead {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new GDSKeywordsDataSource(List(config1))

    assertEquals(List(config1), dataSource.toGDSConfiguration)
  }

  @Test
  def testGDSDisplayedFields {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new GDSKeywordsDataSource(List(config1))

    assertFalse(dataSource.displayedFields.isEmpty)
  }

  @Test
  def testItemToGDSConfiguration {
    val config1 = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val dataSource = new GDSKeywordsDataSource(List(config1))

    assertEquals(config1, dataSource.itemToGDSConfiguration(config1, Nil))

    def itemWrapper(config: GDSConfiguration, item: Item) = {
      
    }
    val wrappers: List[WrappedConfigItem] = List()
    assertEquals(config1, dataSource.itemToGDSConfiguration(config1, wrappers))
  }
}