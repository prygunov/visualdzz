package net.artux.visualdz;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.BitSet;

public class ChannelImage {

  private String name;
  private int width;
  private int height;
  private int swift;
  private BitSet[] rawBytesAsShort;

  public ChannelImage(String name, int width, int height, BitSet[] rawBytesAsShort) {
    this.name = name;
    this.width = width;
    this.height = height;
    this.rawBytesAsShort = rawBytesAsShort;
    setSwift(0);
  }

  public String getName() {
    return name;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setSwift(int swift){
    this.swift = swift;
  }

  public long getVisiblePixel(int x, int y){
    return Bits.convert(rawBytesAsShort[x + y*width].get(2-swift, 10-swift));
  }

  public long getPixel(int x, int y){
    return Bits.convert(rawBytesAsShort[x + y*width].get(0, 10));
  }

  public BufferedImage toImage(){
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();


    for(int i = 0; i < rawBytesAsShort.length; i++) {

      BitSet bitSets = rawBytesAsShort[i].get(swift, 8+swift);

      int brightness = (int) Bits.convert(bitSets);
      targetPixels[i] = ((brightness&0x0ff)<<16)|((brightness&0x0ff)<<8)|(brightness&0x0ff);
    }

    return image;
  }
}
