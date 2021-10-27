package net.artux.visualdz;

import javax.swing.*;

public class ZoomForm extends JFrame{
    public JPanel rootPanel;
    public JCheckBox interpolationCheckBox;
    public JLabel zoomField;
    public JCheckBox brightCheckBox;
    private Image zoomedImage;

    ZoomForm(){
        setContentPane(rootPanel);
        setSize(300, 300);
        setLocation(300, 300);
        setTitle("Лупа");
        setAlwaysOnTop(true);

        interpolationCheckBox.addActionListener(e -> {
            update(zoomedImage);
        });
        brightCheckBox.addActionListener(e -> {
            update(zoomedImage);
        });
    }
    public void setZoomedImage(Image zoomedImage)
    {
        this.zoomedImage = zoomedImage;
        update(zoomedImage);
    }

    public void update(Image image){
        //отрисовка изображения
        if (zoomedImage !=null) {
            if (interpolationCheckBox.isSelected())
                image = image.bilinearInterpolation(15);
            else
                image = image.lochZoom(9);
            if (brightCheckBox.isSelected())
                image = image.normalizeImage();
            zoomField.setIcon(new ImageIcon(image.toBufferedImage()));
        }
        else
            zoomField.setIcon(null);
    }
}
