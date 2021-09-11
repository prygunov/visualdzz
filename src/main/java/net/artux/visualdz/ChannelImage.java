package net.artux.visualdz;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;

public class ChannelImage {

  private int width;
  private int height;
  private int swift;
  private BitSet[] rawBytesAsShort;
  private Short[] bytesAsShort;

  public ChannelImage(int width, int height, BitSet[] rawBytesAsShort) {
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

    /*for (int i = 0; i < bytesAsShort.length; i++) {
      if (swift == 0 && rawBytesAsShort[i]>256)
        bytesAsShort[i] = (short) (rawBytesAsShort[i] / 4);
      else if (swift == 1 && rawBytesAsShort[i]>256)
        bytesAsShort[i] = (short) (rawBytesAsShort[i] / 2);
      else
        bytesAsShort[i] = rawBytesAsShort[i];
    }*/
  }

  public long getPixel(int x, int y){
    return Bits.convert(rawBytesAsShort[x + y*width].get(2-swift, 10-swift));
  }

  public long getRealPixel(int x, int y){
    return Bits.convert(rawBytesAsShort[x + y*width].get(0, 10));
  }

  public Short[] getBytesAsShort() {
    return bytesAsShort;
  }

  public BufferedImage toImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();


    for(int i = 0; i < rawBytesAsShort.length; i++) {

      BitSet bitSets = rawBytesAsShort[i].get(2-swift, 10-swift);

      int brightness = (int) Bits.convert(bitSets);
      targetPixels[i] = ((brightness&0x0ff)<<16)|((brightness&0x0ff)<<8)|(brightness&0x0ff);
    }

    return image;
  }
}
