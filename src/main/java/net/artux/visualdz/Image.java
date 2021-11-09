package net.artux.visualdz;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Image {

    private final int width;
    private final int height;
    private int offset; //переменная сдвига
    private final int beginRow; //переменная начальной строки
    private final short[] brightnessArray; //массив яркостей

    //Конструктор, принимающий значения ширины, высоты, начальной строки и массив яркостей
    //значение сдвига устанавливается 0 по умолчанию
    public Image(int width, int height, int beginRow, short[] brightnessArray, int offset) {
        this.beginRow = beginRow;
        this.width = width;
        this.height = height;
        this.brightnessArray = brightnessArray;
        setOffset(offset);
    }

    public Image(int width, int height, int beginRow, short[] brightnessArray) {
        this(width, height,beginRow,brightnessArray, 0);
    }

    //Геттер ширины
    public int getWidth() {
        return width;
    }

    //Геттер высоты
    public int getHeight() {
        return height;
    }

    //Геттер начальной строки
    public int getBeginRow() {
        return beginRow;
    }

    //Сеттер сдвига
    public void setOffset(int offset) {
        this.offset = offset;
    }

    //Геттер сдвига
    public int getOffset() {
        return offset;
    }

    //Получения значения яркости выбранного пикселя с учетом сдвига
    public int getVisibleBrightness(int i) {
        // побитовое и с числом 255 для представляения в одном байте
        return (brightnessArray[i] >> offset) & 0xff;
    }

    public int getVisibleBrightness(int x, int y) {
        return getVisibleBrightness(x + y * width);
    }

    //Получение значения яркости выбранного пикселя без учёта сдвига
    public short getBrightness(int x, int y) {
        return getBrightness(x + y * width);
    }

    //Получение значения яркости выбранного пикселя без учёта сдвига
    public short getBrightness(int i) {
        return brightnessArray[i];
    }

    public short[] getBrightnessArray() {
        return brightnessArray;
    }

    public short[] getVisibleArray(){
        short[] targetPixels = new short[brightnessArray.length];
        for (int i = 0; i < brightnessArray.length; i++) {
            targetPixels[i] = (short) getVisibleBrightness(i);
        }
        return targetPixels;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        // достаём ссылку на массив пикселей изображения
        int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        // заполняем с помощью массива яркостей
        for (int i = 0; i < brightnessArray.length; i++) {
            int brightness = getVisibleBrightness(i);
            // в одном int(4 байта) можно представить 3 байта цвета
            // реализуется с помощью побитового сдвига
            targetPixels[i] = (brightness << 16) | (brightness << 8) | (brightness);
        }
        return image;
    }

}
