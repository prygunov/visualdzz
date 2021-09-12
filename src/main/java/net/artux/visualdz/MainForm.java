package net.artux.visualdz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MainForm extends JFrame {
    public JPanel rootPanel;
    public JSlider zoomSlider;
    public JButton chooseButton;
    public JTextField xField;
    public JSlider swiftSlider;
    public JTextField yField;
    public JLabel imageFrame;
    public JLabel file;
    public JPanel frame;
    public JScrollPane scrollPane;
    public JTextField factBrightnessField;
    public JTextField brightnessField;
    public JLabel Position;
    public JComboBox filesBox;
    public JLabel fileNameLabel;
    public JLabel sizeLabel;

    MainForm() {
        setContentPane(rootPanel);
        setSize(900, 700);
        setLocation(200, 200);
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
