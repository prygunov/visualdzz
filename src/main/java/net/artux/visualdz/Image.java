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

        int j = 0;
        Pair yPair = new Pair(0, height - 1);
        Pair xPair = new Pair(0, width - 1);

        //определение начальных и последних строк квадрата
        detectLimits(y, yPair, height);
        detectLimits(x, xPair, width);

        for (int i = yPair.first; i <= yPair.last; i++) {
            System.out.println("Row: " + i);
            for (int k = xPair.first; k <= xPair.last; k++) {
                System.out.println("X: " + k);
                part[j] = getBrightness(k, y);
                j++;
            }
        }
        return new Image(side, side, y, part);
    }

    private void detectLimits(int value, Pair pair, int measure){
        if (value >= side / 2 && value + side / 2 <= width) {
            //нормальная ситуация
            pair.first = value - side / 2;
            pair.last = value + side / 2;
        } else if (value < side / 2) {
            //сверху
            pair.last = side - 1;
        } else if (width - side / 2 < value)
            pair.first = height - side - 1;
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

    class Pair{
        public int first;
        public int last;

        public Pair(int first, int last) {
            this.first = first;
            this.last = last;
        }
    }

}
