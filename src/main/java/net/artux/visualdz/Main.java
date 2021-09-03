package net.artux.visualdz;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;

public class Main{

  private static JFrame frame;
  static private JTextArea lastClicked;

  public static void main(String[] args){
    JFrame.setDefaultLookAndFeelDecorated(true);
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        displayImage();
      }
    });
  }

  public static int unsignedToBytes(byte b) {
    return b & 0xFF;
  }

  public static void displayImage() {
    GraphicsOnly app = new GraphicsOnly(new Utils() {
      @Override
      public Image fileToImage(File file) {
        DataInputStream d = null;
        try {
          d = new DataInputStream(new FileInputStream(file));

          byte[] bytes = d.readAllBytes();

          int[] bytesAsInt = new int[bytes.length];
          for (int i = 0; i < bytes.length; i++) {
            bytesAsInt[i] = Byte.toUnsignedInt(bytes[i]);
          }

          int width =  bytesAsInt[1] * 256 + bytesAsInt[0];
          int height = bytesAsInt[3] * 256 + bytesAsInt[2];

          bytesAsInt = new int[bytes.length-4];
          for (int i = 0; i < bytes.length-4; i++) {
            bytesAsInt[i] = Byte.toUnsignedInt(bytes[i]);
          }

          ChannelImage channelImage = new ChannelImage(width, height, bytesAsInt);
          channelImage.setSwift(1);
          return channelImage.toImage();
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }
    });
    JFrame frame = new JFrame("Visual DZ");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(app.getGui());

    frame.setSize(700, 500);
    frame.setLocation(200, 200);
    frame.setVisible(true);
  }


}
