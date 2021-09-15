package net.artux.visualdz;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Image {


  private int width;
  private int height;
  private int offset;
  private int beginRow;
  private final short[] brightnessArray;

  public Image(int width, int height, int beginRow, short[] brightnessArray) {
    this.beginRow = beginRow;
    this.width = width;
    this.height = height;
    this.brightnessArray = brightnessArray;
    setOffset(0);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getBeginRow() {
    return beginRow;
  }

  public void setOffset(int offset){
    this.offset = offset;
  }

  public int getOffset() {
    return offset;
  }

  public int getVisibleBrightness(int i){
    return brightnessArray[i] >> offset;
  }

  public int getVisibleBrightness(int x, int y){
    return getVisibleBrightness(x + y*width);
  }

  public int getBrightness(int x, int y){
    return brightnessArray[x + y*width];
  }

  public BufferedImage toImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    for(int i = 0; i < brightnessArray.length; i++) {
      int brightness = getVisibleBrightness(i);
      targetPixels[i] = (brightness<<16)|(brightness<<8)|(brightness);
    }

    return image;
  }
}
