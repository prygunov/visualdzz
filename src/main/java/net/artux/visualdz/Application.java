package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Application implements ChangeListener {

    List<File> files = new ArrayList<File>();
    private static final String[] formats = {".mbv"};

    MainForm mainForm;

    Dimension size;
    double scale = 1.0;
    private BufferedImage image;

    ChannelImage channelImage;

    ActionListener filesBoxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (File file : files) {
                if (file.getName().equals(mainForm.filesBox.getSelectedItem())) {
                    channelImage = readChannel(file);
                    setImage(channelImage);
                    return;
                }
            }

        }
    };


    Application(){
        mainForm = new MainForm();
        mainForm.chooseButton.addActionListener(e -> {
            files = Arrays.stream(chooseFiles()).filter(new Predicate<File>() {
                @Override
                public boolean test(File file) {
                    for (String format : formats) {
                        if (file.getName().contains(format)) {
                            mainForm.filesBox.addItem(file.getName());

                            return true;
                        }
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        });
        mainForm.filesBox.addActionListener(filesBoxListener);
        JSlider slider = mainForm.zoomSlider;
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        mainForm.swiftSlider.addChangeListener(this);
        mainForm.imageFrame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                mainForm.brightnessField.setText(String.valueOf(channelImage.getPixel(e.getX(), e.getY())));
                mainForm.factBrightnessField.setText(String.valueOf(channelImage.getVisiblePixel(e.getX(), e.getY())));
                mainForm.xField.setText(String.valueOf(e.getX()));
                mainForm.yField.setText(String.valueOf(e.getY()));
            }
        });;
    }

    int getSwift(){
        return mainForm.swiftSlider.getValue();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider){
            JSlider slider = (JSlider) e.getSource();
            if (slider.getMaximum() == 2) {
                if (channelImage != null) {
                    channelImage.setSwift(getSwift());
                    setImage(channelImage);
                }
            } else{
                int value = ((JSlider) e.getSource()).getValue();
                scale = value / 100.0;
            }
            paintImage();

        }

    }

    public void setImage(ChannelImage channelImage) {
        mainForm.fileNameLabel.setText(channelImage.getName());
        mainForm.sizeLabel.setText(channelImage.getWidth() + "X" + channelImage.getHeight());
        this.image = channelImage.toImage();
        paintImage();
    }

    public void drawImage(BufferedImage image) {
        mainForm.imageFrame.setIcon(new ImageIcon(image));
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

            ChannelImage channelImage = new ChannelImage(file.getName(), width, height, bitSets);
            channelImage.setSwift(getSwift());
            return channelImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public File[] chooseFiles(){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);
        int option = fileChooser.showOpenDialog(mainForm);

        if(option == JFileChooser.APPROVE_OPTION)
           return fileChooser.getSelectedFiles();
        else return null;
    }

}
