package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import java.util.logging.Logger
import com.vaadin.terminal.ClassResource
import com.vaadin.event.MouseEvents.{ClickEvent, ClickListener}
import edu.gemini.aspen.gds.web.ui.vaadin.VaadinUtilities._
import com.vaadin.ui._
import themes.BaseTheme
import org.apache.felix.ipojo.annotations.{Requires, Component, Bind, Unbind}
import edu.gemini.aspen.gds.web.ui.api.{StatusPanel, GDSWebModule}

/**
 * Main page of the GDS web UI
 */
@Component(name = "VaadinAppFactory")
class GDSCoreVaadinApp(@Requires statusPanel: StatusPanel) extends Application {
  private val LOG = Logger.getLogger(this.getClass.getName)
  val tabsSheet = new TabSheet()
  val mainWindow = new Window("GDS Management Console")
  val userPanel: Panel = buildUserPanel
  var loginPanel: Panel = buildLoginPanel

  var gdsWebModules = List[TabSheet.Tab]()

  /**
   * Called by Vaadin when the application needs to start
   *
   * In this case we layout the main components
   */
  override def init() {
    LOG.info("GDSCoreVaadinApp init> ")
    setTheme("gds")
    tabsSheet.setHeight("100%")

    val mainLayout = new VerticalLayout
    mainLayout.setMargin(true)
    mainLayout.setSizeFull

    mainLayout.addComponent(buildTopPanel)

    mainLayout.addComponent(tabsSheet)
    mainLayout.setExpandRatio(tabsSheet, 1.0f)

    mainLayout.addComponent(statusPanel.buildStatusPanel)

    mainWindow.setContent(mainLayout)
    setMainWindow(mainWindow)
  }

  /**
   * Listens for modules making up the tabs
   */
  @Bind(optional = true, aggregate = true, specification = "edu.gemini.aspen.gds.web.ui.api.GDSWebModule")
  def bindGDSWebModule(module: GDSWebModule) {
    LOG.info("GDSCoreVaadinApp> tab module detected " + module.title)

    // Adds the tab built by the module
    gdsWebModules = tabsSheet.addTab(module.buildTabContent(mainWindow), module.title, null) :: gdsWebModules
  }

  /**
   * Listens for services gone
   */
  @Unbind(specification = "edu.gemini.aspen.gds.web.ui.api.GDSWebModule")
  def unbindModule(module: GDSWebModule) {
    LOG.info("GDSCoreVaadinApp> tab module gone " + module.title)

    gdsWebModules filter {
      _.getCaption == module.title
    } map {
      tabsSheet.removeTab(_)
    }
    gdsWebModules = gdsWebModules filterNot {
      _.getCaption == module.title
    }
  }

  /**
   * Builds the panel at the top of the application
   */
  private def buildTopPanel: Panel = {
    val topPanel = new Panel
    topPanel.addComponent(loginPanel)
    topPanel.addComponent(userPanel)
    topPanel.addComponent(buildBannerPanel)

    toggleUserBasedVisibilty

    topPanel
  }

  /**
   * Decides what panel to show depending on whether there is a user logged or not
   */
  private def toggleUserBasedVisibilty {
    val user = Option(getUser)

    user map {
      _ =>
        userPanel.setVisible(true)
        loginPanel.setVisible(false)
    } getOrElse {
      userPanel.setVisible(false)
      loginPanel.setVisible(true)
    }
  }

  def buildLoginPanel: Panel = {
    val layout = new HorizontalLayout
    val loginPanel = new Panel(layout)
    val loginLabel = new Label("Login")
    loginLabel.setWidth(null)
    val linkButton = new Button("Login")
    linkButton.setStyleName(BaseTheme.BUTTON_LINK)
    linkButton.setWidth(null)
    linkButton.addListener((e: Button#ClickEvent) => mainWindow.addWindow(new LoginWindow(this)))

    loginPanel.addComponent(linkButton)
    layout.setComponentAlignment(linkButton, Alignment.MIDDLE_RIGHT)
    loginPanel
  }

  def buildUserPanel: Panel = {
    val layout = new HorizontalLayout
    val loginPanel = new Panel(layout)

    val logoutButton = new Button("Logout")
    logoutButton.setStyleName(BaseTheme.BUTTON_LINK)

    loginPanel.addComponent(logoutButton)
    layout.setComponentAlignment(logoutButton, Alignment.MIDDLE_RIGHT)

    loginPanel
  }

  def buildBannerPanel: Panel = {
    val layout = new HorizontalLayout
    val bannerPanel = new Panel(layout)

    // Add the GDS Label
    val gdsLabel = new Label("GIAPI Data Service")
    bannerPanel.addComponent(gdsLabel)
    bannerPanel.setHeight("140px")

    layout.setComponentAlignment(gdsLabel, Alignment.MIDDLE_RIGHT)
    layout.setWidth("100%")

    // Add the logo
    val image = new Embedded("", new ClassResource("gemini-logo.jpg", this))
    image.setHeight("100px")

    bannerPanel
  }

  /**
   * Called whet the user completes authentication
   */
  def authenticated(user: String) {
    this.setUser(user)
    toggleUserBasedVisibilty
  }

}
