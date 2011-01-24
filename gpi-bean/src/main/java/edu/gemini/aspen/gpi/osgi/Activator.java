package edu.gemini.aspen.gpi.osgi;

import edu.gemini.aspen.giapi.status.beans.BaseStatusBean;
import edu.gemini.aspen.gpi.GpiBean;
import edu.gemini.aspen.gpi.gui.DemoControl;
import edu.gemini.aspen.gpi.gui.DemoGui;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.swing.*;

/**
 * Activator class for the Status Database bundle
 */
public class Activator implements BundleActivator {

    private GpiBean _bean;

    private ServiceRegistration _gpiRegistration;



    public void start(BundleContext bundleContext) throws Exception {

        _bean = new GpiBean();

        final DemoGui gui = new DemoGui();
        DemoControl control = new DemoControl(_bean, gui);

        _bean.addPropertyChangeListener(control);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("GPI Status Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                gui.setOpaque(true);
                frame.setContentPane(gui);

                frame.pack();
                frame.setVisible(true);

            }
        });


        //advertise the handler into OSGi, so it start receiving stuff
        _gpiRegistration = bundleContext.registerService(
                BaseStatusBean.class.getName(),
                _bean, null);


    }

    public void stop(BundleContext bundleContext) throws Exception {

        _bean = null;

        _gpiRegistration.unregister();
    }

}