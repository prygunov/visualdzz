package net.artux.visualdz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;


public class GraphicsOnly extends JComponent implements ChangeListener {

  JPanel gui;
  /**
   * Displays the image.
   */
  JLabel imageCanvas;
  Dimension size;
  double scale = 1.0;
  private BufferedImage image;
  private Utils utils;

  ChannelImage channelImage;

  public GraphicsOnly(Utils utils) {
    size = new Dimension(10, 10);
    setBackground(Color.black);
    this.utils = utils;
  }


  public void setImage(BufferedImage image) {
    this.image = image;
  }

  public void drawImage(BufferedImage image) {
    imageCanvas.setIcon(new ImageIcon(image));
  }

  public void initComponents() {
    if (gui == null) {
      gui = new JPanel(new BorderLayout());
      gui.setBorder(new EmptyBorder(5, 5, 5, 5));
      imageCanvas = new JLabel();
      JPanel imageCenter = new JPanel(new GridBagLayout());
      imageCenter.add(imageCanvas);
      JScrollPane imageScroll = new JScrollPane(imageCenter);
      imageScroll.setPreferredSize(new Dimension(300, 100));
      gui.add(imageScroll, BorderLayout.CENTER);

      JPanel left = new JPanel();
      left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
      JLabel label = new JLabel("Файл не выбран.");
      left.add(label);
      gui.add(left, BorderLayout.WEST);

      imageCanvas.addMouseMotionListener(new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {
          String s = "X: " + e.getX() + ", Y: " + e.getY() + ", color: " + channelImage.getPixel(e.getX(), e.getY());
          System.out.println(s);
          label.setText(s);
        }
      });




      JButton button = new JButton("Выбрать файл");



      gui.add(button, BorderLayout.NORTH);
      gui.add(getControl(), BorderLayout.SOUTH);

      button.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();

        int option = fileChooser.showOpenDialog(gui);
        if(option == JFileChooser.APPROVE_OPTION){
          File file = fileChooser.getSelectedFile();
          channelImage = utils.fileToImage(file);
          setImage(channelImage.toImage());
          button.setText("Выбранный файл: " + file.getName());
        }
      });

    }
  }

  public Container getGui() {
    initComponents();
    paintImage();
    return gui;
  }

  protected void paintImage() {
    if (image!=null) {
      int w = getWidth();
      int h = getHeight();
      int imageWidth = image.getWidth();
      int imageHeight = image.getHeight();
      BufferedImage bi = new BufferedImage(
              (int)(imageWidth*scale),
              (int)(imageHeight*scale),
              image.getType());
      Graphics2D g2 = bi.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      double x = (w - scale * imageWidth) / 2;
      double y = (h - scale * imageHeight) / 2;
      AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
      at.scale(scale, scale);
      g2.drawRenderedImage(image, at);
      drawImage(bi);
    }
  }

  public Dimension getPreferredSize() {
    int w = (int) (scale * size.width);
    int h = (int) (scale * size.height);
    return new Dimension(w, h);
  }

  public JSlider getControl() {
    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 50);
    slider.setMajorTickSpacing(50);
    slider.setMinorTickSpacing(25);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(this);
    return slider;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    int value = ((JSlider) e.getSource()).getValue();
    scale = value / 100.0;
    paintImage();
  }
}
