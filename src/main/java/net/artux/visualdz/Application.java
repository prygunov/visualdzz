package net.artux.visualdz;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    private static final String[] supportedFormats = {"mbv"};

    private final MainForm mainForm; // структура окна
    private final ZoomForm zoomForm;
    private final BlurForm blurForm;

    private final List<ImageFile> imageFiles = new ArrayList<>(); // список выбранных файлов-изображений
    private ImageFile chosenImageFile; // последний выбранный файл-изображения
    private int minS = 0, maxS = 255;
    //обработчик выбора полосы изображения
    ActionListener filesBoxChangedListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // находим совпадение имен из списка выбранных файлов
            // и выбранного элемента из выпадающего списка
            for (ImageFile imageFile : imageFiles) {
                if (imageFile.getFile().getName().equals(mainForm.filesBox.getSelectedItem())) {
                    setImage(imageFile);
                    //устанавливаем изображение как выбранное
                    return;
                }
            }

        }
    };

    //обработчик нажатия кнопки отображения
    ActionListener showButtonClickListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // при нажатии отобразить проверяем есть ли выбранное изображение
            if(chosenImageFile != null) {
                int row = (Integer) mainForm.beginRowField.getValue();
                //проверка начальной строки на подходящее значение
                if (row >= 0 && row < chosenImageFile.getHeight()) {
                    try {
                        chosenImageFile.readWithBeginRow(row, mainForm.offsetSlider.getValue());
                        // чтение из памяти изображения в озу, с учетом начальной строки
                        // рисуем изображение
                        renderImage(mainForm.imageFrame, chosenImageFile.getImage());
                    } catch (Exception exception) {
                        // в случае ошибки говорим об этом диалоговым окном
                        JOptionPane.showMessageDialog(mainForm, "Ошибка, не удалось прочесть файл: " + exception.getMessage());
                    }
                } else {
                    // в случае неверного значения начальной строки,
                    // говорим об этом всплывающим окном и устанавливаем 0 по умолчанию
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
                    .filter(file -> {
                        for (String format : supportedFormats) {
                            //проверяем совпадает ли формат файла с поддерживаемыми
                            if (file.getName().contains(format)) {
                                for (ImageFile oldImageFile : imageFiles)
                                    if (file.getName().equals(oldImageFile.getFile().getName()))
                                        return false; // если подобный файл уже был загружен, исключаем из списка

                                return true; // если файл еще не был загружен, оставляем в списке
                            }
                        }
                        return false;// неподдерживаемый формат
                    }).forEach(file -> {
                        try {
                            // добавляем к списку выбранных файл-изображение
                            imageFiles.add(new ImageFile(file));
                            // добавляем имя файла в выпадающий список
                            mainForm.filesBox.addItem(file.getName());
                            if(imageFiles.size() == 1) {
                                mainForm.showButton.setEnabled(true);
                                setImage(imageFiles.get(0));
                            }
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(mainForm, "Ошибка, не удалось определить размер изображения: " + exception.getMessage());
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
                renderImage(mainForm.imageFrame, chosenImageFile.getImage());
            }
        }
    };


    ChangeListener fixedListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (mainForm.lockCheckBox.isSelected()) {
                int dif = mainForm.rightSlider.getValue() - mainForm.leftSlider.getValue();

                mainForm.leftSlider.setMaximum(255-dif);
                mainForm.rightSlider.setMinimum(dif);
            }
        }
    };

    //обработчик изменения сдвига в окне
    ChangeListener minMaxChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if(mainForm.lockCheckBox.isSelected()) {
                int dif;
                if(e.getSource().equals(mainForm.leftSlider)){
                    dif = mainForm.leftSlider.getValue() - minS;
                    if(maxS + dif <= 255)
                    mainForm.rightSlider.setValue(maxS+dif);
                }
                else{
                    dif = mainForm.rightSlider.getValue() - maxS;
                    if(minS + dif >= 0)
                    mainForm.leftSlider.setValue(minS+dif);
                }



            }else{
                mainForm.leftSlider.setMaximum(maxS);
                mainForm.rightSlider.setMinimum(minS);
            }

            mainForm.leftValue.setText(String.valueOf(mainForm.leftSlider.getValue()));
            mainForm.rightValue.setText(String.valueOf(mainForm.rightSlider.getValue()));

            maxS = mainForm.rightSlider.getValue();
            minS = mainForm.leftSlider.getValue();



            if (chosenImageFile != null && chosenImageFile.getImage()!=null) {
                mainForm.updateChart(chosenImageFile.getImage().getVisibleArray(), mainForm.leftSlider.getValue(), mainForm.rightSlider.getValue());
                renderImage(mainForm.imageFrame, chosenImageFile.getImage());
            }
        }
    };


    Application(){
        mainForm = new MainForm();
        zoomForm = new ZoomForm();
        blurForm = new BlurForm();
        // создание окна с элементами

        // назначение обработчиков событий
        mainForm.chooseButton.addActionListener(chooseButtonClickListener);
        mainForm.showButton.addActionListener(showButtonClickListener);
        mainForm.filesBox.addActionListener(filesBoxChangedListener);
        mainForm.offsetSlider.addChangeListener(offsetChangeListener);
        mainForm.leftSlider.addChangeListener(minMaxChangeListener);
        mainForm.rightSlider.addChangeListener(minMaxChangeListener);
        mainForm.lockCheckBox.addChangeListener(fixedListener);

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
        mainForm.blurButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blurForm.setVisible(true);
            }
        });
        mainForm.imageFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    zoomForm.setVisible(true);
                    zoomForm.setZoomedImage(ImageHelper.getPart(chosenImageFile.getImage(), e.getX(), e.getY(), 45));
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    mainForm.updateLineChart(mainForm.chartPanel1, chosenImageFile.getImage().getVisibleArray(e.getY(), true),
                            mainForm.leftSlider.getValue(), mainForm.rightSlider.getValue(), true);


                    mainForm.updateLineChart(mainForm.chartPanel2, chosenImageFile.getImage().getVisibleArray(e.getX(), false),
                            mainForm.leftSlider.getValue(), mainForm.rightSlider.getValue(), false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

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


            renderImage(mainForm.imageFrame, imageFile.getImage());
        } else {
            mainForm.beginRowField.setValue(0);
            renderImage(mainForm.imageFrame, null);
        }
    }

    protected void renderImage(JLabel frame, Image image) {
        //отрисовка изображения
        if (image !=null) {
            mainForm.updateChart(chosenImageFile.getImage().getVisibleArray(),
                    mainForm.leftSlider.getValue(), mainForm.rightSlider.getValue());
            frame.setIcon(new ImageIcon(image.toBufferedImage(mainForm.leftSlider.getValue(), mainForm.rightSlider.getValue(),
                    mainForm.leftSliderBox.getSelectedIndex(), mainForm.rightSliderBox.getSelectedIndex())));
        } else
            frame.setIcon(null);
    }

    public File[] chooseFiles(){
        // окно выбора файлов
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setMultiSelectionEnabled(true);// разрешаем выбор нескольких
        FileNameExtensionFilter filter = new FileNameExtensionFilter("MBV файлы", supportedFormats);
        fileChooser.setFileFilter(filter);// устанавливаем фильтр на формат файлов

        int option = fileChooser.showOpenDialog(mainForm);

        if(option == JFileChooser.APPROVE_OPTION)
           return fileChooser.getSelectedFiles(); // если была нажата кнопка "Open" возвращаем файлы
        else return new File[0];
    }

}
