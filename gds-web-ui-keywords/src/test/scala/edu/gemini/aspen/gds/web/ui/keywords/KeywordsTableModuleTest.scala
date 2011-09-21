package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Assert._
import com.vaadin.Application
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.configuration.{ConfigItem, GDSConfigurationService}
import org.junit.Test

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

    module.buildDataSource(None)

    val tableColumnsForAnonymous = module.visibleColumns(None)
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

    val tableColumnsForUser = module.visibleColumns(Option("user"))
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
    app.getUser returns Some("User")
    module.buildTabContent(app)

    val tableColumnHeaders = module.table.getColumnHeaders
    assertEquals(
      List(
        "Inst.",
        "GDSEvent",
        "FitsKeyword",
        "Header",
        "DataType",
        "Mand.",
        "DefaultValue",
        "Subsystem",
        "Channel",
        "Index",
        "Comment",
        "DEL"),
      tableColumnHeaders.toList)
  }
}