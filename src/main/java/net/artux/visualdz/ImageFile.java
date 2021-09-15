package net.artux.visualdz;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ImageFile {

  private File file;
  private int width;
  private int height;
  private Image image;

  public ImageFile(File file) throws Exception {
    this.file = file;
    defineSize();
  }

  void defineSize() throws Exception {
    DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

    byte[] bytes = dataInputStream.readNBytes(4);
    //прочли байты файла

    Short[] bytesAsShort = new Short[4];
    for (int i = 0; i < 4; i++) {
      bytesAsShort[i] = (short) Byte.toUnsignedInt(bytes[i]);
      // переписываем в неотрицательный массив
    }

    width =  bytesAsShort[0] + bytesAsShort[1] * 256;
    height = bytesAsShort[2] + bytesAsShort[3] * 256;

    dataInputStream.close();
  }

  public void readWithBeginRow(int beginRow, int swift) throws Exception {
    DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
    int skippedBytes = beginRow * width * 2;
    dataInputStream.skipBytes(skippedBytes + 4);

    int renderedHeight= height - beginRow;

    byte[] bytes = dataInputStream.readAllBytes();

    short[] shorts = new short[bytes.length/2];
    for (int i = 0; i < shorts.length; i++) {
      shorts[i] = (short) (Byte.toUnsignedInt(bytes[2 * i]) + 256 * Byte.toUnsignedInt(bytes[2 * i + 1]));
    }

    image = new Image(width, renderedHeight, beginRow, shorts);
    image.setOffset(swift);

    dataInputStream.close();
  }

  public Image getImage() {
    return image;
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
