package net.artux.visualdz;

import javax.swing.*;

public class ZoomForm extends JFrame{
    public JPanel rootPanel;
    public JCheckBox interpolationCheckBox;
    public JLabel zoomField;
    public JCheckBox brightCheckBox;
    public JSlider zoomSlider;
    private Image zoomedImage;

    ZoomForm(){
        setContentPane(rootPanel);
        setLocationByPlatform(true);
        setTitle("Лупа");
        setAlwaysOnTop(true);

        interpolationCheckBox.addActionListener(e -> update(zoomedImage));
        brightCheckBox.addActionListener(e -> update(zoomedImage));
        zoomSlider.addChangeListener(e -> update(zoomedImage));
    }
    public void setZoomedImage(Image zoomedImage)
    {
        this.zoomedImage = zoomedImage;
        update(zoomedImage);
    }

    public void update(Image image){
        int zoom = zoomSlider.getValue();

        if (zoomedImage !=null) {
            if (interpolationCheckBox.isSelected())
                image = ImageHelper.bilinearInterpolation(image, zoom);
            else
                image = ImageHelper.neighbourZoom(image, zoom);
            if (brightCheckBox.isSelected())
                image = ImageHelper.normalizeImage(image);

            zoomField.setIcon(new ImageIcon(image.toBufferedImage()));//отрисовка изображения
            setSize(zoomField.getIcon().getIconWidth() + 30, zoomField.getIcon().getIconHeight() + 160);
        }
        else
            zoomField.setIcon(null);
    }
}
