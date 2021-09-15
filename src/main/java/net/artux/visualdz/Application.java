package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Application {

    private static final String[] supportedFormats = {"mbv"};

    private final MainForm mainForm; // структура окна

    private final List<ImageFile> imageFiles = new ArrayList<>(); // список выбранных файлов-изображений
    private ImageFile chosenImageFile; // последний выбранный файл-изображения

    //обработчик выбора полосы изображения
    ActionListener filesBoxChangedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            // находим совпадение имен из списка выбранных файлов и
            for (ImageFile imageFile : imageFiles) {
                if (imageFile.getFile().getName().equals(mainForm.filesBox.getSelectedItem())) {
                    setImage(imageFile);
                    return;
                }
            }

        }
    };

    //обработчик нажатия кнопки отображения
    ActionListener showButtonClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(chosenImageFile != null) {
                int row = (Integer) mainForm.beginRowField.getValue();
                if (row >= 0 && row < chosenImageFile.getHeight()) {
                    try {
                        chosenImageFile.readWithBeginRow(row, mainForm.offsetSlider.getValue());
                        setImage(chosenImageFile);
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(mainForm, "Ошибка, не удалось прочесть файл: " + exception.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(mainForm, "Начальная строка должна быть не менее 0 и не более " + (chosenImageFile.getHeight() - 1));
                    mainForm.beginRowField.setValue(0);
                }
            }
        }
    };

    //обработчик нажатия кнопки выбора файлов
    ActionListener chooseButtonClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Arrays
                    .stream(chooseFiles())
                    .filter(new Predicate<File>() {
                        @Override
                        public boolean test(File file) {
                            for (String format : supportedFormats) {
                                if (file.getName().contains(format)) {
                                    for (ImageFile oldImageFile : imageFiles)
                                        if (file.getName().equals(oldImageFile.getFile().getName()))
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
                    try {
                        imageFiles.add(new ImageFile(file));
                        mainForm.filesBox.addItem(file.getName());
                        if(imageFiles.size() == 1) {
                            mainForm.showButton.setEnabled(true);
                            setImage(imageFiles.get(0));
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(mainForm, "Ошибка, не удалось определить размер изображения: " + exception.getMessage());
                    }
                }
            });
        }
    };

    //обработчик изменения сдвига в окне
    ChangeListener offsetChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (chosenImageFile.getImage() != null) {
                //если изображение присутствует в озу меняем сдвиг и показываем на экран
                chosenImageFile.getImage().setOffset(mainForm.offsetSlider.getValue());
                renderImage(chosenImageFile.getImage().toImage());
            }
        }
    };


    Application(){
        mainForm = new MainForm(); // создание окна с элементами

        // назначение обработчиков событий
        mainForm.chooseButton.addActionListener(chooseButtonClickListener);
        mainForm.showButton.addActionListener(showButtonClickListener);
        mainForm.filesBox.addActionListener(filesBoxChangedListener);
        mainForm.offsetSlider.addChangeListener(offsetChangeListener);

        // обработчик позиции мыши
        mainForm.imageFrame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                // позиция курсора
                int x = e.getX();
                int y = e.getY();

                // установка значений программы в поля
                mainForm.xField.setText(String.valueOf(x));
                mainForm.yField.setText(String.valueOf(y + chosenImageFile.getImage().getBeginRow()));
                mainForm.factYField.setText(String.valueOf(y));
                mainForm.brightnessField.setText(String.valueOf(chosenImageFile.getImage().getBrightness(x, y)));
                mainForm.factBrightnessField.setText(String.valueOf(chosenImageFile.getImage().getVisibleBrightness(x, y)));
            }
        });
    }

    public void setImage(ImageFile imageFile) {
        //установка отображаемого файла-изображения
        chosenImageFile = imageFile;
        //показ размера файлов
        mainForm.sizeLabel.setText(imageFile.getWidth() + "X" + imageFile.getHeight());

        if (imageFile.getImage()!=null) {
            //если изображение присутствует в озу показываем его на экран и устанавливаем его значения
            mainForm.beginRowField.setValue(imageFile.getImage().getBeginRow());
            mainForm.offsetSlider.setValue(imageFile.getImage().getOffset());
            renderImage(imageFile.getImage().toImage());
        } else {
            mainForm.beginRowField.setValue(0);
            renderImage(null);
        }

    }

    protected void renderImage(BufferedImage visibleImage) {
        //отрисовка изображения
        if (visibleImage !=null)
            mainForm.imageFrame.setIcon(new ImageIcon(visibleImage));
        else
            mainForm.imageFrame.setIcon(null);
    }

    public File[] chooseFiles(){
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MBV файлы", supportedFormats);
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showOpenDialog(mainForm);

        if(option == JFileChooser.APPROVE_OPTION)
           return fileChooser.getSelectedFiles();
        else return new File[0];
    }

}
