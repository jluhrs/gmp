package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import org.apache.felix.ipojo.annotations.{Component, Bind, Unbind}
import java.util.logging.Logger
import com.vaadin.terminal.ClassResource
import com.vaadin.event.MouseEvents.{ClickEvent, ClickListener}
import edu.gemini.aspen.gds.web.ui.vaadin.VaadinUtilities._
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui._
import themes.BaseTheme

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

    def buildStatusPanel = {
        val layout = new HorizontalLayout
        layout.setMargin(false)

        val statusLabel = new Label("Status:")
        val statusState = new Label("Running")

        val filesProcessed = new Label("Files processed:")
        val filesProcessedCount = new Label("1234")

        val filesInProcessed = new Label("Files in process:")
        val filesInProcessedCount = new Label("2")

        layout.addComponent(statusLabel)
        layout.addComponent(statusState)

        layout.addComponent(filesProcessed)
        layout.addComponent(filesProcessedCount)

        layout.addComponent(filesInProcessed)
        layout.addComponent(filesInProcessedCount)

        layout.setComponentAlignment(statusLabel, Alignment.MIDDLE_RIGHT)
        layout.setComponentAlignment(statusState, Alignment.MIDDLE_RIGHT)

        layout.setComponentAlignment(filesProcessed, Alignment.MIDDLE_RIGHT)
        layout.setComponentAlignment(filesProcessedCount, Alignment.MIDDLE_RIGHT)

        layout.setComponentAlignment(filesInProcessed, Alignment.MIDDLE_RIGHT)
        layout.setComponentAlignment(filesInProcessedCount, Alignment.MIDDLE_RIGHT)

        layout.setHeight("30px")
        layout.setWidth("100%")
        layout

        new Panel(layout)
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

    @Bind(optional = true, aggregate = true, specification = "edu.gemini.aspen.gds.web.ui.api.GDSWebModule")
    def bindGDSWebModule(module: GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> tab module detected " + module.title)
        gdsWebModules = tabsSheet.addTab(module.buildTabContent(mainWindow), module.title, null) :: gdsWebModules
    }

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

}
