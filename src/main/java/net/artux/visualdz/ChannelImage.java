package net.artux.visualdz;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.util.Arrays;
import java.util.Collections;

public class ChannelImage {

  private int width;
  private int height;
  private int swift;
  private Short[] rawBytesAsShort;
  private Short[] bytesAsShort;

  public ChannelImage(int width, int height, Short[] rawBytesAsShort) {
    this.width = width;
    this.height = height;
    this.rawBytesAsShort = rawBytesAsShort;
    setSwift(0);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setSwift(int swift){
    this.swift = swift;
    bytesAsShort = new Short[rawBytesAsShort.length];

    for (int i = 0; i < bytesAsShort.length; i++) {
      if (swift == 0 && rawBytesAsShort[i]>256)
        bytesAsShort[i] = (short) (rawBytesAsShort[i] / 4);
      else if (swift == 1 && rawBytesAsShort[i]>256)
        bytesAsShort[i] = (short) (rawBytesAsShort[i] / 2);
      else
        bytesAsShort[i] = rawBytesAsShort[i];
    }
  }

  public Short getPixel(int x, int y){
    return bytesAsShort[x + y*width];
  }

  public Short[] getBytesAsShort() {
    return bytesAsShort;
  }

  public BufferedImage toImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
    short[] targetPixels = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();

    short max = Collections.max(Arrays.asList(bytesAsShort));

    short c = (short) (Short.MAX_VALUE / max);
    System.out.println("C: " + c + " max: " + max);

    for(int i = 0; i < bytesAsShort.length; i++)
      targetPixels[i] = (short) (bytesAsShort[i] * c);

    //System.arraycopy(bytesAsInt, 0, targetPixels, 0, bytesAsInt.length);
    return image;
  }
}
