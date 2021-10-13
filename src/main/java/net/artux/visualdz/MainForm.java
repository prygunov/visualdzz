package net.artux.visualdz;

import javax.swing.*;

public class MainForm extends JFrame {
    public JPanel rootPanel;
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
    public JLabel sizeLabel;
    public JButton showButton;
    public JSpinner beginRowField;
    public JTextField factYField;

    MainForm() {
        setContentPane(rootPanel);
        setSize(900, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(200, 800);
        setVisible(true);
        setTitle("POOVD");


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
