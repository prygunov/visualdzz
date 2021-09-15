package net.artux.visualdz;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Image {

  private File file;
  private int width;
  private int height;
  private ChannelImage channel;

  public Image(File file) {
    this.file = file;
    try {
      defineSize();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void defineSize() throws IOException {
    DataInputStream d;

    d = new DataInputStream(new FileInputStream(file));

    byte[] bytes = d.readNBytes(4);
    //прочли байты файла

    Short[] bytesAsShort = new Short[4];
    for (int i = 0; i < 4; i++) {
      bytesAsShort[i] = (short) Byte.toUnsignedInt(bytes[i]);
      // переписываем в неотрицательный массив
    }

    width =  bytesAsShort[0] + bytesAsShort[1] * 256;
    height = bytesAsShort[2] + bytesAsShort[3] * 256;
    d.close();
  }

  public void readWithBeginRow(int beginRow, int swift) throws IOException {
    DataInputStream d;

    d = new DataInputStream(new FileInputStream(file));
    int skippedBytes = beginRow * width * 2;

    d.skipBytes(skippedBytes + 4);
    int renderedHeight= height - beginRow;

    byte[] bytes = d.readAllBytes();

    short[] shorts = new short[bytes.length/2];
    for (int i = 0; i < shorts.length; i++) {
      short pixel = (short) (Byte.toUnsignedInt(bytes[2 * i]) + 256 * Byte.toUnsignedInt(bytes[2 * i + 1]));
      shorts[i] = pixel;
    }
    ChannelImage channelImage = new ChannelImage(width, renderedHeight, beginRow, shorts);
    channelImage.setOffset(swift);
    this.channel = channelImage;
    d.close();
  }

  public ChannelImage getChannel() {
    return channel;
  }

  public File getFile() {
    return file;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }
}
