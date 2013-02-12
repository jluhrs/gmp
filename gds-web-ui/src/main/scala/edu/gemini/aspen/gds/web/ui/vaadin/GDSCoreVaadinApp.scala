package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import java.util.logging.Logger
import com.vaadin.terminal.ClassResource
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import edu.gemini.aspen.giapi.web.ui.vaadin.data._
import org.apache.felix.ipojo.annotations.{Requires, Bind, Unbind}
import com.vaadin.ui.TabSheet.SelectedTabChangeListener
import edu.gemini.aspen.gds.web.ui.api.{AuthenticationService, GDSWebModuleFactory, StatusPanel, GDSWebModule}
import com.vaadin.ui.{Panel, Window, TabSheet}
import com.vaadin.ui.Alignment

/**
 * Main page of the GDS web UI
 */
@org.apache.felix.ipojo.annotations.Component(name = "VaadinAppFactory")
class GDSCoreVaadinApp(@Requires statusPanel: StatusPanel, @Requires authenticationService: AuthenticationService) extends Application {
  private val LOG = Logger.getLogger(this.getClass.getName)
  val tabsSheet = new TabSheet()
  val mainWindow = new Window("GDS Management Console")
  val userProperty = Property[String]("")
  val userLabel = new Label(property = userProperty)
  val userPanel = buildUserPanel
  var loginPanel = buildLoginPanel

  val gdsWebModules = scala.collection.mutable.Map[GDSWebModuleFactory, (GDSWebModule, TabSheet.Tab)]()

  /**
   * Called by Vaadin when the application needs to start
   *
   * In this case we layout the main components
   */
  override def init() {
    LOG.info("GDSCoreVaadinApp init> ")
    tabsSheet.addListener(new SelectedTabChangeListener {
      def selectedTabChange(event: TabSheet#SelectedTabChangeEvent) {
        val selectedTab = event.getTabSheet.getTab(event.getTabSheet.getSelectedTab)
        val selectedEntry = gdsWebModules filter {
          case (_, (module: GDSWebModule, tab: TabSheet.Tab)) => tab == selectedTab
        }
        selectedEntry.headOption.foreach {
          tab: Tuple2[GDSWebModuleFactory, (GDSWebModule, TabSheet.Tab)] => tab._2._1.refresh(GDSCoreVaadinApp.this)
        }
        statusPanel.refresh
      }
    })
    setTheme("gds")
    tabsSheet.setHeight(100.percent)

    val mainLayout = new VerticalLayout(margin = true, sizeFull = true) {
      add(buildTopPanel)
      add(tabsSheet, ratio = 1.0f)
      add(statusPanel.buildStatusPanel)
    }

    mainWindow.setContent(mainLayout)
    setMainWindow(mainWindow)
  }

  /**
   * Listens for modules making up the tabs
   */
  @Bind(id = "gds-modules", optional = true, aggregate = true, specification = "edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory")
  def bindGDSWebModule(moduleFactory: GDSWebModuleFactory) {
    LOG.info("GDSCoreVaadinApp> tab module factory detected " + moduleFactory)

    // Adds the tab built by the moduleFactory
    val gdsModule = moduleFactory.buildWebModule
    val tabContent = gdsModule.buildTabContent(this)
    tabContent.setDebugId(gdsModule.title)
    val tab = tabsSheet.addTab(tabContent, gdsModule.title, null)
    gdsWebModules += moduleFactory ->(gdsModule, tab)
    putTabsInOrder()
  }

  def sortModules = {
    gdsWebModules.values.toList sortBy {
      case (m, t) => m.order
    }
  }

  def findTabsPositions(sortedModules: List[(GDSWebModule, TabSheet.Tab)]) = {
    for {i <- 0 until sortedModules.size
         (_, t) = sortedModules(i)}
    yield (t, i)
  }

  def putTabsInOrder() {
    val sortedModules = sortModules
    val tabs = findTabsPositions(sortedModules)
    tabs foreach {
      case (t, i) => tabsSheet.setTabPosition(t, i)
    }
    // Set first as selected
    tabs.headOption map {
      case (t, _) => tabsSheet.setSelectedTab(t.getComponent)
    }
  }

  /**
   * Listens for services gone
   */
  @Unbind(id = "gds-modules", specification = "edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory")
  def unbindModule(moduleFactory: GDSWebModuleFactory) {
    LOG.info("GDSCoreVaadinApp> tab module factory gone " + moduleFactory)

    gdsWebModules remove (moduleFactory) foreach {
      case (module, tab) => tabsSheet.removeTab(tab)
    }
    putTabsInOrder()
  }

  /**
   * Builds the panel at the top of the application
   */
  private def buildTopPanel = {
    val layout = new VerticalLayout(margin = false) {
      setDebugId("top-panel")
      add(loginPanel)
      add(userPanel)
      add(buildBannerPanel)
    }

    toggleUserBasedVisibility

    new Panel(layout)
  }

  /**
   * Decides what panel to show depending on whether there is a user logged or not
   */
  private def toggleUserBasedVisibility {
    val user = getUser match {
      case Some(x: String) => Some(x)
      case _ => None
    }

    user map {
      _ =>
        userPanel.setVisible(true)
        loginPanel.setVisible(false)
    } getOrElse {
      userPanel.setVisible(false)
      loginPanel.setVisible(true)
    }
  }

  def buildLoginPanel = {
    val linkButton = new LinkButton("Login", action = _ => mainWindow.addWindow(new LoginWindow(this, authenticationService)))
    linkButton.addStyleName("gds-login-label")

    new HorizontalLayout(width = 100.percent, height = 20.px) {
      setDebugId("login-panel")
      add(linkButton, alignment = Alignment.MIDDLE_RIGHT)
    }
  }

  def buildUserPanel = {
    val logoutButton = new LinkButton("Logout", action = _ => {
      mainWindow.showNotification("Logging out...", Window.Notification.TYPE_WARNING_MESSAGE)
      authenticated(None)
    })
    logoutButton.addStyleName("gds-login-label")

    val subLayout = new HorizontalLayout {
      setDebugId("User-SubPanel")
      val userCaption = new Label("Username: ", style = "gds-user-label")
      userLabel.addStyleName("gds-user-label")
      userLabel.addStyleName("gds-username")
      addComponent(userCaption)
      addComponent(userLabel)
    }

    new HorizontalLayout(width = 100.percent, height = 20.px) {
      setDebugId("User-Panel")

      add(subLayout, alignment = Alignment.MIDDLE_LEFT, ratio = 1.0f)
      add(logoutButton, alignment = Alignment.MIDDLE_RIGHT)
    }
  }

  def buildBannerPanel = {
    // Add the GDS Label
    val gdsLabel = new Label(content = "GIAPI Data Service", style = "gds-title")
    // Add the logo
    val image = new Embedded(source = new ClassResource("gemini-logo.jpg", this), height = 95.px, width = 282.px, style = "gds-title")

    new HorizontalLayout(height = 95.px, width = 100.percent) {
      setDebugId("Banner-Layout")
      add(gdsLabel, alignment = Alignment.MIDDLE_LEFT, ratio = 1.0f)
      add(image, alignment = Alignment.MIDDLE_RIGHT)
    }
  }

  /**
   * Called whet the user completes authentication
   */
  def authenticated(user: Option[String]) {
    this.setUser(user)
    user map {
      userProperty.setValue(_)
    }
    toggleUserBasedVisibility
    // Inform app changes
    gdsWebModules.values.toList map {
      case (m, _) => m.userChanged(user)
    }
  }

}