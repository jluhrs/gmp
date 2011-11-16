package edu.gemini.aspen.gmp.web.ui.console

import com.vaadin.Application;
import java.util.logging.Logger

import scala.collection.JavaConversions._
import com.vaadin.terminal.Sizeable
import com.vaadin.ui.Button.ClickListener
import org.vaadin.console.Console
import org.apache.felix.service.command.CommandProcessor
import java.io.{PrintStream, ByteArrayOutputStream, ByteArrayInputStream}
import org.apache.felix.gogo.runtime.CommandProcessorImpl
import java.util.{Set, Date}
import org.vaadin.console.Console.{Command, Handler}
import org.apache.felix.ipojo.annotations.{Bind, Component, Requires, Invalidate}
import org.osgi.framework.ServiceReference
import com.vaadin.ui._

/**
 * Main page of the GDS web UI
 */
@Component(name = "GMPConsoleApp")
class GMPConsoleApp(@Requires commandProcessor:CommandProcessor) extends Application {
  private val LOG = Logger.getLogger(this.getClass.getName)
  val mainWindow = new Window("GMP Console")

  /**
   * Called by Vaadin when the application needs to start
   *
   * In this case we layout the main components
   */
  override def init() {
    LOG.info("GMPConsoleApp init> ")
    val console = new Console
    console.setGreeting("Welcome to the GMP/Felix console")
    console.setPs("gmp> ")
    val in =  new ByteArrayInputStream(Array[Byte]())
    val bout = new ByteArrayOutputStream
    val out = new PrintStream(bout)
    val err =  new PrintStream(new ByteArrayOutputStream)
    val cp = commandProcessor.createSession(in, out, err)
    val handler = new Handler {
      def commandNotFound(console: Console, commands: Array[String]) {println("brt found")}

      def inputReceived(console: Console, text: String) = {
        println("execute " + text)
        cp.execute(text)
        console.print(new String(bout.toByteArray))
        bout.reset()
        console.newLine()
        console.prompt()
        console.scrollToEnd()
        true
      }

      def handleException(p1: Console, p2: Exception, p3: Command, p4: Array[String]) {println("exc")}

      def getSuggestions(p1: Console, p2: String): Set[String] = null
    }
    //console.setHandler(handler)
    //console.reset()
    console.focus()
    console.setWrap(true)
    console.setSizeFull()
    console.setRows(40)

    val mainLayout = new VerticalLayout
    val label = new Label("GMP/Felix Console")
    val help = new Button("Help")
    val mainPanel = new Panel
    //mainPanel.addComponent(mainLayout)
    //mainLayout.addComponent(label)
    //mainLayout.addComponent(console)
    //mainLayout.setExpandRatio(console, 1.0f)

    mainPanel.setSizeFull()
    mainLayout.setSizeFull()
    mainLayout.setMargin(true)

    mainWindow.addComponent(label)
    mainWindow.addComponent(console)
    mainWindow.addComponent(help)
    mainWindow.setCaption("GMP Console")
    setMainWindow(mainWindow)
  }

  @Bind(optional = true, aggregate = true)
  def bindServiceCommand() {println("o.p")}

  @Invalidate
  def stopListening() {
   // strehlSimulator.stop()
  }


}