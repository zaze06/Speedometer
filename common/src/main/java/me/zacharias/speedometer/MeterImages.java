package me.zacharias.speedometer;

import net.minecraft.network.chat.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public enum MeterImages {
  LARGE(Component.translatable("speedometer.meter.large"), () -> {
    try {
      return ImageIO.read(Objects.requireNonNull(Speedometer.class.getResourceAsStream("/assets/speedometer/meter/meter-115.png")));
    } catch (Exception e) {
      return null;
    }
  }, 115),
  SMALL(Component.translatable("speedometer.meter.small"), () -> {
    try {
      return ImageIO.read(Objects.requireNonNull(Speedometer.class.getResourceAsStream("/assets/speedometer/meter/meter-19.png")));
    } catch (Exception e) {
      return null;
    }
  }, 19),
  MEDIUM(Component.translatable("speedometer.meter.small"), () -> {
    try {
      return ImageIO.read(Objects.requireNonNull(Speedometer.class.getResourceAsStream("/assets/speedometer/meter/meter-67.png")));
    } catch (Exception e) {
      return null;
    }
  }, 67)

  ;

  private final Component name;
  private final BufferedImage image;
  private final int size;

  MeterImages(Component name, Loader icon, int size) {
    this.name = name;
    this.image = icon.load();
    this.size = size;
  }

  public BufferedImage getImage() {
    return image;
  }

  public int getSize() {
    return size;
  }

  public Component getName() {
    return name;
  }

  private interface Loader{
    BufferedImage load();
  }
}
