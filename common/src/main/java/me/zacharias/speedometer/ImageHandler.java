package me.zacharias.speedometer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageHandler {
  public static BufferedImage scale(BufferedImage img, int width, int height) {
    Image img1 = img.getScaledInstance(width,height, Image.SCALE_DEFAULT);
    BufferedImage out = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = out.createGraphics();
    g2d.drawImage(img1,0,0,null);
    return out;
  }
}