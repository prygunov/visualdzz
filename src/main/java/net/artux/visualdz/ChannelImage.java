package net.artux.visualdz;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;

public class ChannelImage {

  private int width;
  private int height;
  private int swift;
  private int[] bytesAsInt;
  private int[] rawBytesAsInt;

  public ChannelImage(int width, int height, int[] rawBytesAsInt) {
    this.width = width;
    this.height = height;
    this.rawBytesAsInt = rawBytesAsInt;
    setSwift(0);
  }

  public void setSwift(int swift){
    this.swift = swift;
    bytesAsInt = new int[rawBytesAsInt.length/2];
    if (swift == 0)
      for (int i = 0; i < bytesAsInt.length; i++)
        bytesAsInt[i] = rawBytesAsInt[2*i];
    else if(swift == 1)
      for (int i = 0; i < bytesAsInt.length; i++)
        bytesAsInt[i] = rawBytesAsInt[2*i] + rawBytesAsInt[2*i+1] *256;
      else
      for (int i = 0; i < bytesAsInt.length; i++)
        bytesAsInt[i] = (byte) rawBytesAsInt[4+2*i];
  }

  public int getSwift() {
    return swift;
  }

  public Image toImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
    short[] targetPixels = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();

    for(int i = 0; i < bytesAsInt.length; i++)
      targetPixels[i] = (short) (bytesAsInt[i] * 100);

    //System.arraycopy(bytesAsInt, 0, targetPixels, 0, bytesAsInt.length);
    return image;
  }
}
