package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.BitSet;

public class Application implements ChangeListener {

    MainForm mainForm;

    Dimension size;
    double scale = 1.0;
    private BufferedImage image;

    ChannelImage channelImage;

    Application(){
        mainForm = new MainForm();
        mainForm.getChooseButton().addActionListener(e -> {
            channelImage = readChannel(chooseFile());
            setImage(channelImage.toImage());
        });

        JSlider slider = mainForm.getZoomSlider();
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        mainForm.getSwift().addChangeListener(this);
        mainForm.getImageFrame().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                mainForm.getBrightnessField().setText(String.valueOf(channelImage.getPixel(e.getX(), e.getY())));
                mainForm.getFactBrightnessField().setText(String.valueOf(channelImage.getRealPixel(e.getX(), e.getY())));
                mainForm.getxField().setText(String.valueOf(e.getX()));
                mainForm.getyField().setText(String.valueOf(e.getY()));
            }
        });;
    }

    int getSwift(){
        return mainForm.getSwift().getValue();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider){
            JSlider slider = (JSlider) e.getSource();
            if (slider.getMaximum() == 2) {
                if (channelImage != null) {
                    channelImage.setSwift(getSwift());
                    setImage(channelImage.toImage());
                }
            } else{
                int value = ((JSlider) e.getSource()).getValue();
                scale = value / 100.0;
            }
            paintImage();

        }

    }

    public void setImage(BufferedImage image) {
        this.image = image;
        paintImage();
    }

    public void drawImage(BufferedImage image) {
        mainForm.getImageFrame().setIcon(new ImageIcon(image));
    }

    protected void paintImage() {
        if (image!=null) {
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            BufferedImage scaledImage = new BufferedImage(
                    (int)(imageWidth*scale),
                    (int)(imageHeight*scale),
                    image.getType());
            Graphics2D g2 = scaledImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
            at.scale(scale, scale);
            g2.drawRenderedImage(image, at);
            drawImage(scaledImage);
        }
    }

    public ChannelImage readChannel(File file){
        DataInputStream d = null;
        try {
            d = new DataInputStream(new FileInputStream(file));

            byte[] bytes = d.readAllBytes();
            //прочли байты файла

            Short[] bytesAsShort = new Short[4];
            for (int i = 0; i < 4; i++) {
                bytesAsShort[i] = (short) Byte.toUnsignedInt(bytes[i]);
                // переписываем в неотрицательный массив
            }

            int width =  bytesAsShort[0] + bytesAsShort[1] * 256;
            int height = bytesAsShort[2] + bytesAsShort[3] * 256;

            bytesAsShort = new Short[(bytes.length-4)/2];
            BitSet[] bitSets = new BitSet[(bytes.length-4)/2];
            for (int i = 0; i < bitSets.length; i++) {
                bitSets[i] = new BitSet(10);
                bitSets[i] = Bits.convert((Byte.toUnsignedInt(bytes[4 + 2 * i]) + 256L * Byte.toUnsignedInt(bytes[5 + 2 * i])));
            }

            ChannelImage channelImage = new ChannelImage(width, height, bitSets);
            channelImage.setSwift(getSwift());
            return channelImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public File chooseFile(){
        JFileChooser fileChooser = new JFileChooser();

        int option = fileChooser.showOpenDialog(mainForm);
        if(option == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        else return null;
    }

}
