package net.artux.visualdz;

import javax.swing.*;

public class ZoomForm extends JFrame{
    public JPanel rootPanel;
    public JCheckBox interpolationCheckBox;
    public JLabel zoomField;
    public JCheckBox brightCheckBox;

    ZoomForm(){
        setContentPane(rootPanel);
        setSize(150, 200);
        setLocation(300, 300);
        setTitle("Лупа");
    }
}
