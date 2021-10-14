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

    short[] setValue(short[] arr, int x, int y, short value, int width){
        arr[x + y * width] = value;
        return arr;
    }

    public Image bilinearInterpolation(int mult){
        short[] newarr = new short[brightnessArray.length * mult * mult ];
        int nwidth = width * mult;
        int nheight = height * mult;
        int firstPos = mult/2;
        for(int y = 0;y<height;y++) {
            for(int x =0 ;x<width;x++) {
                int nx = x * mult + firstPos;
                int ny = y * mult + firstPos;
                newarr = setValue(newarr, nx, ny, getBrightness(x,y), nwidth);
            }
        }

        for (int squareY = 0; squareY < width - 1; squareY++) {
            for (int squareX = 0; squareX < height - 1; squareX++){
                int skipedY1 = nwidth + squareY*nwidth * mult; // пропуск строк до первой нужной
                int skipedY2 = skipedY1 + nwidth * mult;

                int skipedX1 = firstPos + squareX*mult;
                int skipedX2 = firstPos + (squareX + 1) * mult;

                int i1 = newarr[skipedY1 + skipedX1],
                        i2 = newarr[skipedY1 + skipedX2],
                        i3 = newarr[skipedY2 + skipedX1],
                        i4 = newarr[skipedY2 + skipedX2];
                int d = i1, a = i2 - d, b = i3-d, c = i4 - a - b - d;
                double yL = 0;
                for (int y = skipedY1; y < skipedY2; y+=nwidth) {
                    double xL = 0;
                    for (int x = skipedX1; x < skipedX2; x++) {
                        newarr[x + y] = (short)(a * xL + b * yL + c * xL * yL -d);
                        xL += 1.0/mult;
                    }
                    yL += 1.0/mult;
                }
            }
        }

        return new Image(width*mult,height*mult,0,newarr);
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


    public Image getPart(int x, int y, int side) {
        short[] part = new short[side * side];

        int j = 0;
        Pair yPair = new Pair(0, height - 1);
        Pair xPair = new Pair(0, width - 1);

        //определение начальных и последних строк квадрата
        detectLimits(y, yPair, height);
        detectLimits(x, xPair, width);

        for (int i = yPair.first; i <= yPair.last; i++) {
            for (int k = xPair.first; k <= xPair.last; k++) {
                part[j] = getBrightness(k, y);
                j++;
            }
        }
        return new Image(side, side, y, part);
    }

    private void detectLimits(int value, Pair pair, int side){
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
