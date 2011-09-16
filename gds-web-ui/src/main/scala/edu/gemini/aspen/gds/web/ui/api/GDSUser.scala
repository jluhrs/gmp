package edu.gemini.aspen.gds.web.ui.api

import org.apache.felix.ipojo.annotations.{Validate, Property, Provides, Component}
import java.io.File
import xml.XML
import edu.gemini.aspen.gds.web.ui.api.AuthenticationService

case class GDSUser(username:String, roles: String, password:String)


