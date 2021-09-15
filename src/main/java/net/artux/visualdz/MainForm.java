package net.artux.visualdz;

import javax.swing.*;

public class MainForm extends JFrame {
    public JPanel rootPanel;
    public JSlider zoomSlider;
    public JButton chooseButton;
    public JTextField xField;
    public JSlider offsetSlider;
    public JTextField yField;
    public JLabel imageFrame;
    public JPanel frame;
    public JScrollPane scrollPane;
    public JTextField factBrightnessField;
    public JTextField brightnessField;
    public JLabel Position;
    public JComboBox<String> filesBox;
    public JLabel fileNameLabel;
    public JLabel sizeLabel;
    public JButton showButton;
    public JSpinner beginRowField;

    MainForm() {
        setContentPane(rootPanel);
        setSize(900, 700);
        setLocation(200, 200);
        setVisible(true);
        setTitle("POOVD 1");


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
