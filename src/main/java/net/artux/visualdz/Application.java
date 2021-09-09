package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Application implements ChangeListener {

    MainForm mainForm;

    Dimension size;
    double scale = 1.0;
    private BufferedImage image;
    private Utils utils;

    ChannelImage rChannel;
    ChannelImage gChannel;
    ChannelImage bChannel;

    Application(){
        mainForm = new MainForm();
        mainForm.getrButton().addActionListener(e -> rChannel = readChannel(chooseFile()));
        mainForm.getgButton().addActionListener(e -> gChannel = readChannel(chooseFile()));
        mainForm.getbButton().addActionListener(e -> {
            bChannel = readChannel(chooseFile());
            Channels channels = new Channels(rChannel, gChannel, bChannel);
            setImage(channels.toImage());
        });


        JSlider slider = mainForm.getZoomSlider();
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
    }

    int getSwift(){
        return mainForm.getSwift().getValue();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider){
            JSlider slider = (JSlider) e.getSource();
            switch (slider.getName()){
                case "zoomSlider":
                    int value = ((JSlider) e.getSource()).getValue();
                    scale = value / 100.0;
                    break;
                case "swiftSlider":
                    if (rChannel!=null)
                        rChannel.setSwift(getSwift());
                    if (gChannel!=null)
                        gChannel.setSwift(getSwift());
                    if (bChannel!=null)
                        bChannel.setSwift(getSwift());

                    break;
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
            BufferedImage bi = new BufferedImage(
                    (int)(imageWidth*scale),
                    (int)(imageHeight*scale),
                    image.getType());
            Graphics2D g2 = bi.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
            at.scale(scale, scale);
            g2.drawRenderedImage(image, at);
            drawImage(bi);
        }
    }

    public Dimension getPreferredSize() {
        int w = (int) (scale * size.width);
        int h = (int) (scale * size.height);
        return new Dimension(w, h);
    }

    public ChannelImage readChannel(File file){
        DataInputStream d = null;
        try {
            d = new DataInputStream(new FileInputStream(file));

            byte[] bytes = d.readAllBytes();

            Short[] bytesAsInt = new Short[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                bytesAsInt[i] = (short) Byte.toUnsignedInt(bytes[i]);
            }

            int width =  bytesAsInt[1] * 256 + bytesAsInt[0];
            int height = bytesAsInt[3] * 256 + bytesAsInt[2];

            bytesAsInt = new Short[(bytes.length-4)/2];
            for (int i = 0; i < (bytes.length-4)/2; i++) {
                bytesAsInt[i] = (short) (Byte.toUnsignedInt(bytes[4 + 2 * i]) + 256 * Byte.toUnsignedInt(bytes[5 + 2 * i]));
            }

            ChannelImage channelImage = new ChannelImage(width, height, bytesAsInt);
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
