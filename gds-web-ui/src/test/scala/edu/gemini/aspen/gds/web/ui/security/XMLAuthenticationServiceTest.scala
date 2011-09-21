package edu.gemini.aspen.gds.web.ui.security

import org.junit.Test
import org.junit.Assert._
import org.specs2.mock.Mockito

class XMLAuthenticationServiceTest extends Mockito {
  val testFile = getClass.getResource("testfile.xml").toURI.getPath
  val userProvider = new XMLAuthenticationService(testFile)
  
  @Test
  def testUser() {
    //assertTrue(userProvider.authenticate("admin", "adminpassword"))
    assertTrue(userProvider.authenticate("user", "password"))
  }
}