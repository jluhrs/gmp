package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Assert._
import com.vaadin.Application
import edu.gemini.aspen.gds.api.configuration.{ConfigItem, GDSConfigurationService}
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scala.collection.JavaConversions._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

/**
 * Construction tests
 */
@RunWith(classOf[JUnitRunner])
class KeywordsTableModuleTest extends FunSuite with MockitoSugar {
  test("build panel") {
    // mock configuration service
    val configService = mock[GDSConfigurationService]
    when(configService.getFullConfiguration).thenReturn(List[ConfigItem[_]]())
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    assertNotNull(module.statusRow(app))
  }

  test("buttons hidden for anonymous user, test for GIAPI-868") {
    val configService = mock[GDSConfigurationService]
    when(configService.getFullConfiguration).thenReturn(List[ConfigItem[_]]())
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    module.buildTabContent(app)
    val buttonsVisibility = module.statusRow(app).getComponentIterator collect {
      case c if c.getDebugId == "gds-web-ui-keywords.status.newrow" => c.isVisible
      case c if c.getDebugId == "gds-web-ui-keywords.status.save" => c.isVisible
    }
    assertEquals(List(false, false), buttonsVisibility.toList)
  }

  test("buttons displayed for valid user, test for GIAPI-868") {
    val configService = mock[GDSConfigurationService]
    when(configService.getFullConfiguration).thenReturn(List[ConfigItem[_]]())
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    when(app.getUser).thenAnswer(new Answer[Some[_]]() {
      override def answer(invocationOnMock:InvocationOnMock) = Some("User")
    })
    module.buildTabContent(app)
    val buttonsVisibility = module.statusRow(app).getComponentIterator collect {
      case c if c.getDebugId == "gds-web-ui-keywords.status.newrow" => c.isVisible
      case c if c.getDebugId == "gds-web-ui-keywords.status.save" => c.isVisible
    }
    assertEquals(List(true, true), buttonsVisibility.toList)
  }

  test("visible columns") {
    val configService = mock[GDSConfigurationService]
    when(configService.getFullConfiguration).thenReturn(List[ConfigItem[_]]())
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

  test("column headers") {
    val configService = mock[GDSConfigurationService]
    when(configService.getFullConfiguration).thenReturn(List[ConfigItem[_]]())
    val module = new KeywordsTableModule(configService)

    val app = mock[Application]
    when(app.getUser).thenAnswer(new Answer[Some[_]]() {
      override def answer(invocationOnMock:InvocationOnMock) = Some("User")
    })
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