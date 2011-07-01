package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import org.apache.felix.ipojo.annotations.{Component, Bind, Unbind}
import java.util.logging.Logger
import com.vaadin.terminal.ClassResource
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.event.MouseEvents.{ClickEvent, ClickListener}
import com.vaadin.ui._
import themes.BaseTheme
import edu.gemini.aspen.gds.web.ui.vaadin.VaadinUtilities._

/**
 * Main page of the GDS web UI
 */
@Component(name = "VaadinAppFactory")
class GDSCoreVaadinApp extends Application {
    private val LOG = Logger.getLogger(this.getClass.getName)
    val tabsSheet = new TabSheet()
    val mainWindow = new Window("GDS Management Console")
    val userPanel: Panel = buildUserPanel
    var loginPanel: Panel = buildLoginPanel

    var gdsWebModules = List[TabSheet.Tab]()

    def buildStatusPanel: Panel = {
        val statusBar = new Panel()
        statusBar.setHeight("40px")
        statusBar
    }

    def toggleUserBasedVisibilty {
        val user = Option(getUser)

        user map { _ =>
            userPanel.setVisible(true)
            loginPanel.setVisible(false)
        } getOrElse {
            userPanel.setVisible(false)
            loginPanel.setVisible(true)
        }
    }

    def buildTopPanel: Panel = {
        val topPanel = new Panel
        topPanel.addComponent(loginPanel)
        topPanel.addComponent(userPanel)
        topPanel.addComponent(buildBannerPanel)

        toggleUserBasedVisibilty

        topPanel
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

        //loginPanel.addComponent(loginLabel)
        loginPanel.addComponent(linkButton)
        //layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_RIGHT)
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
        //layout.setMargin(true)
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

    def authenticated(user: String) {
        this.setUser(user)
        toggleUserBasedVisibilty
        //val loginParent = loginPanel.getParent.asInstanceOf[ComponentContainer]
        //loginParent.removeComponent(loginPanel)
        //topPanel.removeComponent(loginPanel)
    }

    override def init() {
        LOG.info("GDSCoreVaadinApp init> ")
        tabsSheet.setHeight("100%")

        val mainLayout = new VerticalLayout
        mainLayout.setMargin(true)
        mainLayout.setSizeFull

        mainLayout.addComponent(buildTopPanel)

        mainLayout.addComponent(tabsSheet)
        mainLayout.setExpandRatio(tabsSheet, 1.0f)

        mainLayout.addComponent(buildStatusPanel)

        mainWindow.setContent(mainLayout)
        setMainWindow(mainWindow)
    }

    @Bind(optional = true, aggregate = true)
    def bindGDSWebModule(module: GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module detected " + module.title)
        gdsWebModules = tabsSheet.addTab(module.buildTabContent, module.title, null) :: gdsWebModules
    }

    @Unbind(optional = true, aggregate = true)
    def unbindModule(module: GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module gone " + module.title)

        gdsWebModules filter {
            _.getCaption == module.title
        } map {
            tabsSheet.removeTab(_)
        }
        gdsWebModules = gdsWebModules filterNot {
            _.getCaption == module.title
        }
    }

}
