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
            update();
        });
    }
    public void setZoomedImage(Image zoomedImage)
    {
        this.zoomedImage = zoomedImage;
        update();
    }

    void update(){
        if(interpolationCheckBox.isSelected()){
            render(zoomedImage.bilinearInterpolation(15));
        }else
            render(zoomedImage);
    }

    public void render(Image image){
        //отрисовка изображения
        if (zoomedImage !=null)
            zoomField.setIcon(new ImageIcon(image.toBufferedImage()));
        else
            zoomField.setIcon(null);
    }
}
