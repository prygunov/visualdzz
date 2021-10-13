package net.artux.visualdz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ZoomForm extends JFrame{
    public JPanel rootPanel;
    public JCheckBox interpolationCheckBox;
    public JLabel zoomField;
    public JCheckBox brightCheckBox;
    private Image cuttedImage;

    ZoomForm(){
        setContentPane(rootPanel);
        setSize(150, 200);
        setLocation(300, 300);
        setTitle("Лупа");

        interpolationCheckBox.addActionListener(e -> {
            if(interpolationCheckBox.isSelected()){
                Image increasedImage = cuttedImage.bilinearInterpolation(7);
                zoomField.setIcon(new ImageIcon(increasedImage.toBufferedImage()));
            }
        });
    }
    public void setCuttedImage(Image cuttedImage)
    {
        this.cuttedImage = cuttedImage;
    }
}
