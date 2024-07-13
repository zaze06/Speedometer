package me.zacharias.speedometer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageHandler {
  public static BufferedImage scale(BufferedImage img, int width, int height) {
    Image img1 = img.getScaledInstance(width,height, Image.SCALE_DEFAULT);
    BufferedImage out = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = out.createGraphics();
    g2d.drawImage(img1,0,0,null);
    return out;
  }

  public static BufferedImage clone(BufferedImage image) {
    BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = out.createGraphics();
    for(int x = 0; x < image.getWidth(); x++){
      for(int y = 0; y < image.getWidth(); y++){
        g2d.setColor(new Color(image.getRGB(x,y)));
        g2d.fillRect(x,y,1,1);
      }
    }
    return out;
  }
  
  public static BufferedImage rotate(BufferedImage img, double angle) {
    double rads = Math.toRadians(angle);
    int w = img.getWidth();
    int h = img.getHeight();
    
    BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = rotated.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate(w / 2d, h / 2d);
    
    int x = w / 2;
    int y = h / 2;
    
    at.rotate(rads, x, y);
    g2d.setTransform(at);
    g2d.drawImage(img, 0, h, null);
    g2d.dispose();
    
    return rotated;
  }
}