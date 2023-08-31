package me.zacharias.speedometer;

import net.minecraft.network.chat.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static me.zacharias.speedometer.Speedometer.LOGGER;

public enum MeterImages {
  LARGE(Component.translatable("speedometer.meter.large"), "/assets/speedometer/meter/meter-115.png", 115),
  SMALL(Component.translatable("speedometer.meter.small"), "/assets/speedometer/meter/meter-19.png", 19),
  MEDIUM(Component.translatable("speedometer.meter.small"), "/assets/speedometer/meter/meter-67.png", 67)

  ;

  private final Component name;
  private final String meterIcon;
  private BufferedImage image;
  private final int size;

  MeterImages(Component name, String meterIcon, int size) {
    this.name = name;
    this.size = size;
    this.meterIcon = meterIcon;
  }

  public boolean initiate(){
    if(image != null){
      LOGGER.warn("Already loaded \""+meterIcon+"\"");
    }
    try{
      LOGGER.info("Loading speedometer \""+meterIcon+"\"");
      image = ImageIO.read(Objects.requireNonNull(Speedometer.class.getResourceAsStream(meterIcon)));
      LOGGER.info("Loaded speedometer \""+meterIcon+"\"");
      return true;
    } catch (IOException e) {
      image = new BufferedImage(0,0, BufferedImage.TYPE_INT_ARGB);
      LOGGER.warn("Failed to load speedometer \""+meterIcon+"\"");
      return false;
    }
  }

  public BufferedImage getImage() {
    if(image == null){
      LOGGER.warn("\""+meterIcon+"\" has not ben loaded yet!");
    }
    return image;
  }

  public int getSize() {
    return size;
  }

  public Component getName() {
    return name;
  }
}
