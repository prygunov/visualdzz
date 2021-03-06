package net.artux.visualdz;

public class ImageHelper {


    public static Image neighbourZoom(Image image, int mult){
        short[] newarr = new short[image.getBrightnessArray().length * mult * mult];
        int nw = image.getWidth() * mult;

        int curY = 0;
        for(int y = 0;y<image.getHeight()*mult;y++) {
            int curX = 0;
            for(int x =0 ;x<image.getWidth()*mult;x++) {
                if(x != 0 && x%mult == 0) curX++;
                newarr[y * nw + x] = image.getBrightness(curX, curY);
            }
            if(y != 0 && y%mult == 0) curY++;
        }
        return new Image(image.getWidth()*mult, image.getHeight()*mult, 0, newarr, image.getOffset());
    }

    private static void setValue(short[] arr, int x, int y, short value, int width){
        arr[x + y * width] = value;
    }


    public static Image bilinearInterpolation(Image image, int mult){
        int nw = image.getWidth() * mult - mult + 1;
        int nh = image.getHeight() * mult - mult + 1;
        // новая ширина и высота без полос по краям

        short[] newarr = new short[nw * nh];
        for(int y = 0;y<image.getHeight();y++) {
            int ny = y * mult;

            for(int x =0 ;x<image.getWidth();x++) {
                int nx = x * mult;
                setValue(newarr, nx, ny, image.getBrightness(x, y), nw);
            }
        }

        for (int squareY = 0; squareY < image.getWidth() - 1; squareY++) {
            for (int squareX = 0; squareX < image.getHeight() - 1; squareX++){
                // пропуск строк до первой нужной
                int skipedY1 = squareY * nw * mult;
                int skipedY2 = skipedY1 + nw * mult;

                //пропуск стобцов
                int skipedX1 = squareX*mult;
                int skipedX2 = (squareX + 1) * mult;

                int i2 = newarr[skipedY1 + skipedX2],
                        i3 = newarr[skipedY2 + skipedX1],
                        i4 = newarr[skipedY2 + skipedX2];
                //определяем 4 точки квадрата
                int d = newarr[skipedY1 + skipedX1], a = i2 - d, b = i3-d, c = i4 - a - b - d;
                double yInSquare = 0;
                //заполнение точек внутри квадрата
                for (int y = skipedY1; y <= skipedY2; y+= nw) {
                    double xInSquare = 0;
                    for (int x = skipedX1; x <= skipedX2; x++) {
                        double nX = a * xInSquare/mult + b * yInSquare/mult + c * xInSquare/mult * yInSquare/mult +d;
                        newarr[x + y] = (short)nX;
                        xInSquare += 1;
                    }
                    yInSquare += 1;
                }
            }
        }
        return new Image(nw, nh,0, newarr, image.getOffset());
    }

    public static Image getPart(Image image, int x, int y, int side) {
        Pair yPair = new Pair(0, image.getHeight() - 1);
        Pair xPair = new Pair(0, image.getWidth() - 1);

        //определение координат квадрата
        detectLimits(y, yPair, side, image.getHeight());
        detectLimits(x, xPair, side, image.getWidth());

        short[] part = new short[side * side];

        int j = 0;
        for (y = yPair.first; y <= yPair.last; y++) {
            for ( x = xPair.first; x <= xPair.last; x++) {
                // копирование в новый массив
                part[j] = image.getBrightness(x, y);
                j++;
            }
        }
        // создание новой картинки используя вырезанный массив
        return new Image(side, side, y, part, image.getOffset());
    }

    private static void detectLimits(int value, Pair pair, int side, int limit){
        if (value - side / 2 >= 0 && value + side / 2 <= limit-1) {
            //нормальная ситуация
            pair.first = value - side / 2;
            pair.last = value + side / 2;
        } else if (value <= side / 2) {
            //сверху
            pair.last = side - 1;
        } else if (limit - side / 2 <= value)
            pair.first = limit - side;
    }



    public static Image normalizeImage(Image image) {
        short[] normPixels = new short[image.getBrightnessArray().length];

        short min = 1023, max = 0;
        for (short pixel : image.getBrightnessArray()) {
            max = (short) Math.max(max, pixel);
            min = (short) Math.min(min, pixel);
        }

        double mult = 255.0 / (max - min);
        for (int i = 0; i < normPixels.length; i++) {
            normPixels[i] = (short) ((image.getBrightness(i) - min) * mult);
        }
        return new Image(image.getWidth(),image.getHeight(),0,normPixels);
    }

    static class Pair{
        public int first;
        public int last;

        public Pair(int first, int last) {
            this.first = first;
            this.last = last;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", last=" + last +
                    '}';
        }
    }

}
