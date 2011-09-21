package edu.gemini.aspen.gds.web.ui.security

import org.apache.felix.ipojo.annotations.{Validate, Property, Provides, Component}
import java.io.File
import xml.XML
import edu.gemini.aspen.gds.web.ui.api.{GDSUser, AuthenticationService}
import java.util.logging.Logger

/**
 * Implementation of Authentication Service that takes an XML file containing the users/passwords */
@Component
@Provides(specifications = Array(classOf[AuthenticationService]))
class XMLAuthenticationService(@Property(name = "usersXMLFile", value = "NOVALID", mandatory = true) filename: String) extends AuthenticationService {
  private val LOG = Logger.getLogger(this.getClass.getName)
  private val encoders = Map("SHA-1" -> new SHA1Encoder())
  LOG.info("XML Authenthication Service starting with file:" + filename)

  val (encoder, users) = parseFile(filename)

  private def parseFile(filename: String) = {
    val f = new File(filename)
    require(f.exists)
    require(f.isFile)
    require(f.canRead)

    val src = XML.loadFile(filename)
    val encoder = (src \\ "passwordencoder").text
    val userAttributes = for {user@ <user>{_*}</user> <- (src \\ "user")}
      yield (user.attribute("username"), user.attribute("roles"), user.attribute("password"))

    // Discard invalid values
    val userData = userAttributes collect {
      case (Some(username), Some(roles), Some(password)) => username.text -> new GDSUser(username.text, roles.text, password.text)
    }
    (encoders.getOrElse(encoder, new DefaultEncoder()), userData.toMap)
  }


  @Validate
  def start() {
    // Required for ipojo
  }

  /**
   * Authenticate a given user */
  override def authenticate(username: String, password: String) = findUser(username) match {
    case Some(GDSUser(_, _, storedPassword)) if (encoder.encode(password) == storedPassword) => true
    case _ => false
  }

  private def findUser(username: String) = users.get(username)

}