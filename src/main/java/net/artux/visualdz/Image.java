package net.artux.visualdz;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Image {

    private int width;
    private int height;
    private int offset; //переменная сдвига
    private int beginRow; //переменная начальной строки
    private final short[] brightnessArray; //массив яркостей

    //Конструктор, принимающий значения ширины, высоты, начальной строки и массив яркостей
    //значение сдвига устанавливается 0 по умолчанию
    public Image(int width, int height, int beginRow, short[] brightnessArray) {
        this.beginRow = beginRow;
        this.width = width;
        this.height = height;
        this.brightnessArray = brightnessArray;
        setOffset(0);
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
        return brightnessArray[x + y * width];
    }

    int side = 5;

    public Image getPart(int x, int y) {
        System.out.println(x + ":" + y);
        short[] part = new short[side * side];
        int index = x + width * y;

        int j = 0;
        Integer beginRow = 0;
        Integer lastRow = height - 1;

        Integer beginColumn = 0;
        Integer lastColumn = width - 1;

        //определение начальных и последних строк квадрата
        detectLimits(y, beginRow, lastRow, height);
        detectLimits(x, beginColumn, lastColumn, width);

        for (int i = beginRow; i <= lastRow; i++) {
            System.out.println("Row: " + i);
            for (int k = beginColumn; k <= lastColumn; k++) {
                System.out.println("X: " + k);
                part[j] = getBrightness(k, y);
            }
        }
        return new Image(side, side, y, part);
    }

    private void detectLimits(int value, Integer first, Integer last, int measure){
        if (value >= side / 2 && value + side / 2 <= width) {
            //нормальная ситуация
            first = value - side / 2;
            last = value + side / 2;
        } else if (value < side / 2) {
            //сверху
            last = side - 1;
        } else if (width - side / 2 < value)
            first = height - side - 1;
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
