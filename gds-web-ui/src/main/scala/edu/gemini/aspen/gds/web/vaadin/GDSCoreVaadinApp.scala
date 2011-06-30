package edu.gemini.aspen.gds.web.vaadin

import com.vaadin.Application;
import org.apache.felix.ipojo.annotations.{Component, Bind, Unbind}
import java.util.logging.Logger
import edu.gemini.aspen.gds.web.api.GDSWebModule
import com.vaadin.terminal.{ClassResource, StreamResource}
import com.vaadin.ui._

/**
 * Main page of the GDS web UI
 */
@Component(name = "VaadinAppFactory")
class GDSCoreVaadinApp extends Application {
    private val LOG = Logger.getLogger(this.getClass.getName)
    val tabsSheet = new TabSheet()
    val mainWindow = new Window("GDS Management Console")
    var list = List[TabSheet.Tab]()

    def buildStatusPanel: Panel = {
        val statusBar = new Panel()
        statusBar.setHeight("40px")
        statusBar
    }

    def buildTopPanel: Panel = {
        val layout = new HorizontalLayout
        layout.setMargin(true)
        val topPanel = new Panel(layout)
        val gdsLabel = new Label("GIAPI Data Service")
        topPanel.addComponent(gdsLabel)
        topPanel.setHeight("140px")

        layout.setComponentAlignment(gdsLabel, Alignment.MIDDLE_RIGHT)
        layout.setWidth("100%")

        val image = new Embedded("", new ClassResource("gemini-logo.jpg", this))
        image.setHeight("100px")
        //topPanel.addComponent(image)
        //layout.setComponentAlignment(image, Alignment.TOP_LEFT)

        topPanel
    }

    override def init() {
        LOG.info("GDSCoreVaadinApp init> ")
        tabsSheet.setHeight("100%")

        val mainLayout = new VerticalLayout
        mainLayout.setMargin(true)
        mainLayout.setSizeFull
        mainWindow.setContent(mainLayout)

        val topPanel = buildTopPanel
        val statusPanel = buildStatusPanel

        mainLayout.addComponent(topPanel)
        mainLayout.addComponent(tabsSheet)
        mainLayout.setExpandRatio(tabsSheet, 1.0f);
        mainLayout.addComponent(statusPanel)
        setMainWindow(mainWindow)

    }

    @Bind(optional = true, aggregate = true)
    def bindGDSWebModule(module: GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module detected " + module.title)
        list = tabsSheet.addTab(module.buildTabContent, module.title, null) :: list
    }

    @Unbind(optional = true, aggregate = true)
    def unbindModule(module: GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module gone " + module.title)

        val tabsToRemove = list filter {
            _.getCaption == module.title
        } map {
            tabsSheet.removeTab(_)
        }
        list = list filterNot {
            _.getCaption == module.title
        }
    }

}
