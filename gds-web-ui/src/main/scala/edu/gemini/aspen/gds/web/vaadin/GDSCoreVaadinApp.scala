package edu.gemini.aspen.gds.web.vaadin

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;
import org.apache.felix.ipojo.annotations._
import edu.gemini.epics.EpicsReader
import java.util.logging.Logger
import edu.gemini.aspen.gds.web.api.GDSWebModule
import collection.script.Remove

/**
 * Main page of the GDS web UI
 */
@Component(name = "VaadinAppFactory")
class GDSCoreVaadinApp extends Application {
    private val LOG = Logger.getLogger(this.getClass.getName)
    val tabsSheet = new TabSheet()
    var list = List[TabSheet.Tab]()

    override def init() {
        LOG.info("GDSCoreVaadinApp init> ")
        tabsSheet.setSizeFull()

        setMainWindow(new Window("GDS Management Console", tabsSheet))
    }

    @Bind(optional = true, aggregate = true)
    def bindGDSWebModule(module:GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module detected " + module.title)
        list = tabsSheet.addTab(module.buildTabContent, module.title, null) :: list
    }

    @Unbind(optional = true, aggregate = true)
    def unbindModule(module:GDSWebModule) {
        LOG.info("GDSCoreVaadinApp> module gone " + module.title)

        val tabsToRemove = list filter {_.getCaption == module.title} map {tabsSheet.removeTab(_)}
        list = list filterNot {_.getCaption == module.title}
    }

}
