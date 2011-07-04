package edu.gemini.aspen.gds.web.ui.vaadin

import org.junit.Test
import org.junit.Assert._

class CSSResourcesTest {
  @Test
  def trivialTest {
    val css = new CSSResources
    assertEquals("/VAADIN/themes/gds" ,css.getAlias)
    assertEquals("/css" ,css.getPath)
  }
}

class ImagesResourcesTest {
  @Test
  def trivialTest {
    val css = new ImageResources
    assertEquals("/gds/APP/1" ,css.getAlias)
    assertEquals("/images" ,css.getPath)
  }
}