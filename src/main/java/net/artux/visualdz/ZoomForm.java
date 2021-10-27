package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

        interpolationCheckBox.addActionListener(e -> {
            update(zoomedImage);
        });
        brightCheckBox.addActionListener(e -> {
            update(zoomedImage);
        });
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                update(zoomedImage);
            }
        });

    }
    public void setZoomedImage(Image zoomedImage)
    {
        this.zoomedImage = zoomedImage;
        update(zoomedImage);
    }

    public void update(Image image){
        int zoom = zoomSlider.getValue();
        //отрисовка изображения
        if (zoomedImage !=null) {
            if (interpolationCheckBox.isSelected())
                image = image.bilinearInterpolation(zoom);
            else
                image = image.lochZoom(zoom);
            if (brightCheckBox.isSelected())
                image = image.normalizeImage();
            zoomField.setIcon(new ImageIcon(image.toBufferedImage()));
            setSize(zoomField.getIcon().getIconWidth() + 30, zoomField.getIcon().getIconHeight() + 160);
        }
        else
            zoomField.setIcon(null);
    }
}
