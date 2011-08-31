package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api.configuration.{ConfigItem, GDSConfigurationService}

/**
 * Construction tests
 */
class KeywordsTableModuleTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock configuration service
    val configService = mock[GDSConfigurationService]
    configService.getFullConfiguration returns List[ConfigItem[_]]()
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    assertNotNull(module.statusRow(app))
  }

  @Test
  def testVisibleColumns() {
    val configService = mock[GDSConfigurationService]
    configService.getFullConfiguration returns List[ConfigItem[_]]()
    val module = new KeywordsTableModule(configService)

    val tableColumnsForAnonymous = module.visibleColumns(null)
    assertArrayEquals(
      Array[AnyRef](
        "Instrument",
        "GDSEvent",
        "FitsKeyword",
        "HeaderIndex",
        "DataType",
        "Mandatory",
        "DefaultValue",
        "Subsystem",
        "Channel",
        "ArrayIndex",
        "FitsComment"),
      tableColumnsForAnonymous)

    val tableColumnsForUser = module.visibleColumns("user")
    assertArrayEquals(
      Array[AnyRef](
        "Instrument",
        "GDSEvent",
        "FitsKeyword",
        "HeaderIndex",
        "DataType",
        "Mandatory",
        "DefaultValue",
        "Subsystem",
        "Channel",
        "ArrayIndex",
        "FitsComment",
        "DEL"),
      tableColumnsForUser)
  }

  @Test
  def testColumnHeaders() {
    val configService = mock[GDSConfigurationService]
    configService.getFullConfiguration returns List[ConfigItem[_]]()
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    module.buildTabContent(app)

    val tableColumnHeaders = module.table.getColumnHeaders
    assertEquals(
      List(
        "Instrument",
        "GDSEvent",
        "FitsKeyword",
        "HeaderIndex",
        "DataType",
        "Mandatory",
        "DefaultValue",
        "Subsystem",
        "Channel",
        "ArrayIndex",
        "FitsComment"),
      tableColumnHeaders.toList)
  }
}