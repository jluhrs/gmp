package edu.gemini.aspen.gpi.gui;

import edu.gemini.aspen.gpi.Filter;
import edu.gemini.aspen.gpi.GpiBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Apr 15, 2010
 * Time: 3:42:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DemoControl implements PropertyChangeListener {


    private DemoGui gui;
    private GpiBean bean;

    public DemoControl(GpiBean bean, DemoGui gui) {

        this.gui = gui;
        this.bean = bean;
        gui.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        gui.getLabel().setOpaque(false);

    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {


        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                Filter f = bean.getFilter();

                if (f != null) {
                    gui.getLabel().setText(f.name());
                    Color c = Color.RED;
                    switch (f) {

                        case RED:
                            c = Color.RED;
                            break;
                        case BLUE:
                            c = Color.BLUE;
                            break;
                        case GRAY:
                            c = Color.GRAY;
                            break;
                    }
                    gui.getLabel().setForeground(c);
                }


                int status1 = bean.getStatus1();
                int status2 = bean.getStatus2();


                gui.getStatus().setText(Integer.toString(status1));
                gui.getStatus2().setText(Integer.toString(status2));

            }


        });

    }
}
