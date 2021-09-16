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
  public void setOffset(int offset){
    this.offset = offset;
  }
  //Геттер сдвига
  public int getOffset() {
    return offset;
  }
  //Получения значения яркости выбранного пикселя с учетом сдвига
  public int getVisibleBrightness(int i){
    return brightnessArray[i] >> offset;
  }

  public int getVisibleBrightness(int x, int y){
    return getVisibleBrightness(x + y*width);
  }
  //Получение значения яркости выбранного пикселя без учёта сдвига
  public int getBrightness(int x, int y){
    return brightnessArray[x + y*width];
  }

  public BufferedImage toBufferedImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    //достаём ссылку на массив пикселей изображения и заполняем с помощью массива яркостей
    int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    for(int i = 0; i < brightnessArray.length; i++) {
      int brightness = getVisibleBrightness(i);
      //
      targetPixels[i] = (brightness<<16)|(brightness<<8)|(brightness);
    }

    return image;
  }
}
