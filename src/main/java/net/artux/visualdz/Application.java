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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Application implements ChangeListener {


    private static final String[] formats = {".mbv"};

    private MainForm mainForm;

    private List<Image> images = new ArrayList<>();
    private double scale = 1.0;
    private BufferedImage visibleImage;
    private Image chosenImage;

    ActionListener filesBoxListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (Image image : images) {
                if (image.getFile().getName().equals(mainForm.filesBox.getSelectedItem())) {
                    setImage(image);
                    return;
                }
            }

        }
    };

    ActionListener showButtonListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                chosenImage.readWithBeginRow((Integer) mainForm.beginRowField.getValue(), mainForm.swiftSlider.getValue());
                setImage(chosenImage);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    };


    Application(){
        mainForm = new MainForm();
        mainForm.chooseButton.addActionListener(e -> {
            Arrays
                    .stream(chooseFiles())
                    .filter(new Predicate<File>() {
                        @Override
                        public boolean test(File file) {
                            for (String format : formats) {
                                if (file.getName().contains(format)) {
                                    for (Image oldImage : images)
                                        if (file.getName().equals(oldImage.getFile().getName()))
                                            return false;
                                    //проверяем есть ли в массиве файл с именем загружаемого файла

                                    return true;
                                }
                            }
                            return false;
                        }
                    }).forEach(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            mainForm.filesBox.addItem(file.getName());
                            images.add(new Image(file));
                        }
                    });

            //добавляем в наш массив файлов файлы, которые только что считали
        });
        mainForm.showButton.addActionListener(showButtonListener);
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
                int x = (int) (e.getX() / scale);
                int y = (int) (e.getY() / scale);
                mainForm.xField.setText(String.valueOf(x));
                mainForm.yField.setText(String.valueOf(y));
                mainForm.brightnessField.setText(String.valueOf(chosenImage.getChannel().getPixel(x, y)));
                mainForm.factBrightnessField.setText(String.valueOf(chosenImage.getChannel().getVisiblePixel(x, y)));
            }
        });
    }

    int getSwift(){
        return mainForm.swiftSlider.getValue();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider){
            JSlider slider = (JSlider) e.getSource();
            if (slider.getMaximum() == 2) {
                if (chosenImage.getChannel() != null) {
                    chosenImage.getChannel().setSwift(getSwift());
                    setImage(chosenImage);
                }
            } else{
                int value = ((JSlider) e.getSource()).getValue();
                scale = value / 100.0;
                renderImage();
            }
        }

    }

    public void setImage(Image image) {
        chosenImage = image;
        mainForm.sizeLabel.setText(image.getWidth() + "X" + image.getHeight());
        if (image.getChannel()!=null) {
            this.visibleImage = image.getChannel().toImage();
            renderImage();
        }
    }

    protected void renderImage() {
        if (visibleImage !=null) {
            int imageWidth = visibleImage.getWidth();
            int imageHeight = visibleImage.getHeight();
            BufferedImage scaledImage = new BufferedImage(
                    (int)(imageWidth*scale),
                    (int)(imageHeight*scale),
                    visibleImage.getType());
            Graphics2D g2 = scaledImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
            at.scale(scale, scale);
            g2.drawRenderedImage(visibleImage, at);
            mainForm.imageFrame.setIcon(new ImageIcon(scaledImage));
        }
    }

    public File[] chooseFiles(){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);
        int option = fileChooser.showOpenDialog(mainForm);

        if(option == JFileChooser.APPROVE_OPTION)
           return fileChooser.getSelectedFiles();
        else return new File[0];
    }

}
