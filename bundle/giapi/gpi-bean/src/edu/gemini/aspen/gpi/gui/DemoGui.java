package edu.gemini.aspen.gpi.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Apr 15, 2010
 * Time: 3:38:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DemoGui extends JPanel {

    private JLabel text;
    private JLabel status1;
    private JLabel status2;
    private JButton ok;


    public JLabel getLabel() {
        return text;
    }


    public JLabel getStatus() {
        return status1;
    }

    public JLabel getStatus2() {
        return status2;
    }

    public JButton getButton() {
        return ok;
    }

    public DemoGui() {
        super (new GridLayout(0, 1));

        text = new JLabel();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Filter: "), BorderLayout.WEST);
        panel.add(text, BorderLayout.EAST);

        status1 = new JLabel();
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(new JLabel("Status: "), BorderLayout.WEST);
        panel2.add(status1, BorderLayout.EAST);

        status2 = new JLabel();
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(new JLabel("Status: "), BorderLayout.WEST);
        panel3.add(status2, BorderLayout.EAST);

        ok = new JButton("Ok");

        add(panel);
        add(panel2);
        add(panel3);
        add(ok);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }


}
