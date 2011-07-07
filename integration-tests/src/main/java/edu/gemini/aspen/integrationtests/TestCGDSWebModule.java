package edu.gemini.aspen.integrationtests;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule;

abstract public class TestCGDSWebModule implements GDSWebModule {

    @Override
    public Component buildTabContent(Window window) {
        return new Panel();
    }
}
