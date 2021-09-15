package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Application {

    private static final String[] formats = {"mbv"};

    private final MainForm mainForm;

    private final List<Image> images = new ArrayList<>();
    private Image chosenImage;

    ActionListener filesBoxChangedListener = new ActionListener() {
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

    ActionListener showButtonClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = (Integer)mainForm.beginRowField.getValue();
            if(chosenImage != null && row >= 0 && row < chosenImage.getHeight()) try {
                chosenImage.readWithBeginRow(row, mainForm.offsetSlider.getValue());
                setImage(chosenImage);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            else{
                mainForm.beginRowField.setValue(Integer.valueOf(0));
            }
        }
    };

    ActionListener chooseButtonClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
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
                    if(images.size() == 1 )setImage(images.get(0));
                }
            });
        }
    };

    ChangeListener offsetChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (chosenImage.getChannel() != null) {
                chosenImage.getChannel().setSwift(mainForm.offsetSlider.getValue());
                setImage(chosenImage);
            }
        }
    };


    Application(){
        mainForm = new MainForm();

        mainForm.chooseButton.addActionListener(chooseButtonClickListener);
        mainForm.showButton.addActionListener(showButtonClickListener);
        mainForm.filesBox.addActionListener(filesBoxChangedListener);
        mainForm.offsetSlider.addChangeListener(offsetChangeListener);

        mainForm.imageFrame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                // позиция курсора
                mainForm.xField.setText(String.valueOf(x));
                mainForm.yField.setText(String.valueOf(y + chosenImage.getChannel().getBeginRow()));
                mainForm.factYField.setText(String.valueOf(y));
                mainForm.brightnessField.setText(String.valueOf(chosenImage.getChannel().getPixel(x, y)));
                mainForm.factBrightnessField.setText(String.valueOf(chosenImage.getChannel().getVisiblePixel(x, y)));
            }
        });
    }

    public void setImage(Image image) {
        chosenImage = image;
        mainForm.sizeLabel.setText(image.getWidth() + "X" + image.getHeight());

        if (image.getChannel()!=null) {
            mainForm.beginRowField.setValue(image.getChannel().getBeginRow());
            renderImage(image.getChannel().toImage());
        } else {
            mainForm.beginRowField.setValue(0);
            renderImage(null);
        }

    }

    protected void renderImage(BufferedImage visibleImage) {
        if (visibleImage !=null)
            mainForm.imageFrame.setIcon(new ImageIcon(visibleImage));
        else
            mainForm.imageFrame.setIcon(null);
    }

    public File[] chooseFiles(){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MBV файлы", formats);
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showOpenDialog(mainForm);

        if(option == JFileChooser.APPROVE_OPTION)
           return fileChooser.getSelectedFiles();
        else return new File[0];
    }

}
