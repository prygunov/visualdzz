package net.artux.visualdz;

import javax.swing.*;

public class Main{

  static final double[] steps ={0.1, 0.01, 0.001, 0.0025};

  public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName()); // получение  и установка для программы стиля системы
    new Application(); // запуск приложения

  }

  static void get(){
    for (int i = 0; i < steps.length; i++) {
      double h = steps[i];
      double t = 0;
      int n = 0;
      while(t<=1){
        System.out.println("t: " + t + " != 1 :"+ (t != 1));
        t=t+h;
        n++;
      }
      double d = 1/h;
      System.out.println(h + " : " + (1/h + 1) + " : " + n);
    }

  }

}
