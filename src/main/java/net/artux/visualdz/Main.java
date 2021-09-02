package net.artux.visualdz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {

  private static JFrame frame;
  static private JTextArea lastClicked;

  public static void main(String[] args){
    JFrame.setDefaultLookAndFeelDecorated(true);
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createGUI();
      }
    });
  }

  public static void createGUI()
  {
    frame = new JFrame("Visual DZ");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JLabel label = new JLabel("Test label");
    frame.getContentPane().add(label);

    frame.setPreferredSize(new Dimension(800, 600));

    JButton button = new JButton("Switch");

    frame.getContentPane().setLayout(new GridBagLayout());

    frame.add(button);

    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if(option == JFileChooser.APPROVE_OPTION){
          File file = fileChooser.getSelectedFile();
          try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            System.out.println(Arrays.toString(bytes));
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }
          label.setText("File Selected: " + file.getName());
        }else{
          label.setText("Open command canceled");
        }
      }
    });


    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

}
