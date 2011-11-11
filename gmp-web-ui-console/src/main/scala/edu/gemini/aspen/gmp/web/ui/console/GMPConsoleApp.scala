package edu.gemini.aspen.gmp.web.ui.console

import com.vaadin.Application;
import java.util.logging.Logger

import com.vaadin.ui.themes.BaseTheme
import com.vaadin.ui.TabSheet.SelectedTabChangeListener
import java.util.{Calendar, Date}
import scala.collection.JavaConversions._
import collection.mutable.LinkedHashSet
import java.io.Serializable
import com.vaadin.terminal.{Sizeable, ClassResource}
import org.apache.felix.ipojo.annotations.{Invalidate, Requires, Bind, Unbind}
import com.vaadin.data.util.{HierarchicalContainer, ObjectProperty}
import com.vaadin.ui._
import java.lang.{String, Boolean}
import com.vaadin.ui.Button.ClickListener
import com.vaadin.data.Item
import org.vaadin.console.Console

/**
 * Main page of the GDS web UI
 */
@org.apache.felix.ipojo.annotations.Component(name = "GMPConsoleApp")
class GMPConsoleApp extends Application {
  private val LOG = Logger.getLogger(this.getClass.getName)
  val STREHL = "Strehl"
  val R0 = "R0"
  val NAME_PROPERTY = "NAME"
  val VALUE_PROPERTY = "VALUE"
  val GRAPH_PROPERTY = "GRAPH"

  val AOC_STATS = "AOC Statistics"

  val mainWindow = new Window("GMP Console")
    // Add Table columns
    val dataSource = new HierarchicalContainer

    dataSource.addContainerProperty(NAME_PROPERTY, classOf[String], "")
    dataSource.addContainerProperty(VALUE_PROPERTY, classOf[String], "")
    dataSource.addContainerProperty(GRAPH_PROPERTY, classOf[CheckBox], null)
    val aocStatistics = dataSource.addItem(AOC_STATS)
    val aocStatR0 = dataSource.addItem(R0)
    val aocStatStrehl = dataSource.addItem(STREHL)

  /**
   * Called by Vaadin when the application needs to start
   *
   * In this case we layout the main components
   */
  override def init() {
    LOG.info("GMPConsoleApp init> ")
    val dataPanelLayout = new VerticalLayout

    val mainLayout = new HorizontalSplitPanel
    mainLayout.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE)

    val treetable = new TreeTable()
    treetable.setSizeFull()
    aocStatStrehl.getItemProperty(VALUE_PROPERTY).setValue("0.00")
    aocStatStrehl.getItemProperty(NAME_PROPERTY).setValue(STREHL)
    val strehlCheck = new CheckBox("Shown")
    strehlCheck.setValue(true)
    strehlCheck.addListener(new ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        if (strehlCheck.getValue == false) {
          strehlCheck.setCaption("Hidden")
        } else {
          strehlCheck.setCaption("Shown")
        }
      }
    })
    aocStatStrehl.getItemProperty(GRAPH_PROPERTY).setValue(strehlCheck)

    aocStatR0.getItemProperty(VALUE_PROPERTY).setValue("0.00")
    aocStatR0.getItemProperty(NAME_PROPERTY).setValue(R0)
    val r0Check = new CheckBox("Hidden")
    r0Check.setValue(false)
    r0Check.addListener(new ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        if (r0Check.getValue == false) {
          r0Check.setCaption("Hidden")
        } else {
          r0Check.setCaption("Shown")
        }
      }
    })

    aocStatR0.getItemProperty(GRAPH_PROPERTY).setValue(r0Check)

    aocStatistics.getItemProperty(NAME_PROPERTY).setValue(AOC_STATS)

    dataSource.setParent(STREHL, AOC_STATS)
    dataSource.setParent(R0, AOC_STATS)
    dataSource.setChildrenAllowed(STREHL, false)
    dataSource.setChildrenAllowed(R0, false)
    
    treetable.setContainerDataSource(dataSource)

    dataPanelLayout.addComponent(treetable)
    treetable.setCollapsed(AOC_STATS, false)

    mainLayout.setFirstComponent(dataPanelLayout)
    val console = new Console
    console.setSizeFull()
    console.setPs("gmp >")
    console.setGreeting("Welcome to the GMP/Felix console")
    val dateCommand = new Console.Command() {

                override def getUsage(console:Console, argv:Array[String]) = {
                    argv(0) + ": prints the current date and time."
                }

                override def execute(console:Console, argv:Array[String]) {
                    return ""+new Date()
                }
            }

    mainLayout.setSecondComponent(console)

    mainLayout.setSizeFull

    mainWindow.setContent(mainLayout)
    mainWindow.setCaption("AO Performance")
    setMainWindow(mainWindow)
    //splineThread.start()
  }



  @Invalidate
  def stopListening() {
   // strehlSimulator.stop()
  }


}