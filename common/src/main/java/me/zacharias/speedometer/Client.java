package me.zacharias.speedometer;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static me.zacharias.speedometer.Speedometer.LOGGER;
import static me.zacharias.speedometer.Speedometer.MOD_ID;

public class Client {
  public static final KeyMapping CONFIG_KEY = new KeyMapping(
      "speedometer.key.configKey",
      InputConstants.Type.KEYSYM,
      InputConstants.KEY_O,
      "speedometer.key.category"
  );
  public static final KeyMapping DEBUG_KEY = new KeyMapping(
      "speedometer.key.debugKey",
      InputConstants.Type.KEYSYM,
      InputConstants.KEY_F6,
      "speedometer.key.category"
  );

  public static final KeyMapping SPEED_KEY = new KeyMapping(
      "speedometer.key.speedKey",
      InputConstants.Type.KEYSYM,
      InputConstants.KEY_UP,
      "speedometer.key.category"
  );

  private static final ArrayList<Double> speeds = new ArrayList<>();
  private static boolean speedometerVisualDisplayFailed = false;
  public static BufferedImage img = null;

  public static void init(){

    Platform.getMod(MOD_ID).registerConfigurationScreen(parent -> ConfigMenu.getConfig(parent).build());

    KeyMappingRegistry.register(CONFIG_KEY);
    ClientTickEvent.CLIENT_POST.register(minecraft -> {
      if(CONFIG_KEY.consumeClick()){
        Minecraft.getInstance().setScreen(ConfigMenu.getConfig(Minecraft.getInstance().screen).build());
      }
    });

    KeyMappingRegistry.register(DEBUG_KEY);
    ClientTickEvent.CLIENT_POST.register(minecraft -> {
      if(DEBUG_KEY.consumeClick()){
        Config.setDebug(!Config.isDebug());
      }
    });

    //KeyMappingRegistry.register(SPEED_KEY);
    ClientTickEvent.CLIENT_POST.register(minecraft -> {
      if(SPEED_KEY.consumeClick()){
        if(minecraft.player != null) {
          minecraft.player.addDeltaMovement(new Vec3(1, 0, 0));
        }
      }
    });

    Config.initialize();
    Config.save();

    ClientGuiEvent.RENDER_HUD.register(Client::render);

    LOGGER.info("Loading speedometer ");
    if(!MeterImages.LARGE.initiate()){
      speedometerVisualDisplayFailed = true;
    }

    LOGGER.info("Finished loading speedometer");
  }

  private static void render(GuiGraphics graphics, float tick) {
    if(Minecraft.getInstance().player == null) return;
    Entity entity = Minecraft.getInstance().player.getRootVehicle();

    Vec3 vec = entity.getDeltaMovement();

    double yOffset = 0.0784000015258789D;
    double xOffset = 0D;
    double zOffset = 0D;
    double vOffset = 0D;

    if (entity instanceof Player e) {
      if (!e.onGround() && e.isCreative()) {
        yOffset = 0;
      } else if (e.isInWater()) {
        yOffset = 0.005;
      }
    } else if (entity instanceof Boat || entity instanceof Minecart || entity instanceof Pig) {
      yOffset = 0;
    }

    double speed = (Math.sqrt(Math.pow(vec.x + xOffset, 2) + Math.pow(vec.y + yOffset, 2) + Math.pow(vec.z + zOffset, 2)) * 20)+vOffset;
    double lSpeed = speed;

    if (speeds.size() >= 30) {
      speeds.remove(0);
    }
    speeds.add(speed);
    speed = 0;
    for (Double aDouble : speeds) {
      speed += aDouble;
    }
    speed = speed / speeds.size();

    double speedTypeSpeed;

    SpeedTypes speedType = Config.getSpeedType();
    if (speedType == SpeedTypes.KNOT || (entity instanceof Boat && Config.getUseKnot())) {
      speedTypeSpeed = speed * 1.94384449;
    }else if (speedType == SpeedTypes.KMPH) {
      speedTypeSpeed = speed * 3.6;
    } else if (speedType == SpeedTypes.MPH) {
      speedTypeSpeed = speed * 2.23693629;
    }else {
      speedTypeSpeed = speed;
    }

    String format = String.format("%.2f", speedTypeSpeed);

    //double v = (Math.pow(1.0233435, speedTypeSpeed)-1)/100;
    double v = switch (speedType){
      case KMPH -> Math.pow(speedTypeSpeed,0.87)-1;
      case BlockPS, MPS -> Math.pow(speedTypeSpeed,1.25);
      case MPH -> speedTypeSpeed;
      case KNOT -> Math.pow(speedTypeSpeed,1.05);
    }/100;
    double i = (v *(316-45))+45;

    if(Config.getVisualSpeedometer() && !speedometerVisualDisplayFailed){

      //double v = speedTypeSpeed / speedType.gatMaxVisual();

      img = ImageHandler.clone(MeterImages.LARGE.getImage());

      Graphics2D g2d = img.createGraphics();

      g2d.setColor(new Color(138, 0, 0));
      g2d.setFont(new Font(g2d.getFont().getName(), Font.PLAIN, 15));
      g2d.drawString(SpeedTypes.getName(speedType).getString(), img.getWidth()/2-27,img.getHeight()/2+25);

      BufferedImage img = ImageHandler.scale(Client.img, Config.getImageSize(), Config.getImageSize());

      int radius = img.getWidth()/2-4;

      int x3 = (int) Math.round(radius*Math.cos(Math.toRadians(i+90)))+(img.getWidth()/2);
      int y3 = (int) Math.round(radius*Math.sin(Math.toRadians(i+90)))+(img.getHeight()/2);

      g2d = img.createGraphics();

      g2d.setColor(new Color(138, 0, 0));

      g2d.setStroke(new BasicStroke(2));

      g2d.drawLine(x3,y3,img.getWidth()/2,img.getHeight()/2);

      int xPos = getPos(graphics, Config.getXPosition(), 0);
      int yPos = getPos(graphics, Config.getYPosition(), 1);

      for(int x1 = 0; x1 < img.getWidth(); x1++){
        for(int y1 = 0; y1 < img.getHeight(); y1++){
          int x2 = x1 + xPos - img.getWidth();
          int y2 = y1 + yPos - img.getHeight();

          int rgb = img.getRGB(x1, y1);

          if(new Color(rgb).equals(Color.black)) continue;

          graphics.fill(x2, y2, x2+1, y2+1, rgb);
        }
      }

      if(i >= 360+45){
        String string = "x" + (int)Math.floor(i/(365+45));
        graphics.drawString(
            Minecraft.getInstance().font,
            string,
            xPos-Minecraft.getInstance().font.width(string),
            (int)(yPos-4.5-(Config.getImageSize()/2)),
            new Color(138, 0, 0).getRGB()
        );
      }

    }else {
      // i -> x
      // j -> y
      // k -> color RGB int
      String speedString = format + " " + SpeedTypes.getName(speedType).getString();
      int width = Minecraft.getInstance().font.width(speedString);
      int lineHeight = Minecraft.getInstance().font.lineHeight;
      graphics.drawString(
          Minecraft.getInstance().font,
          speedString,
          getPos(graphics, Config.getXPosition(), 0) - width,
          getPos(graphics, Config.getYPosition(), 1) - lineHeight,
          Config.getColor().getColor()
      );
    }

    if(Config.isDebug()){
      String debugData = "Velocity raw:" + "\n" +
          "  X: " + vec.x + "\n" +
          "  Y: " + vec.y + "\n" +
          "  Z: " + vec.z + "\n" +
          "Offsets:" + "\n" +
          "  X: " + xOffset + "\n" +
          "  Y: " + yOffset + "\n" +
          "  Z: " + zOffset + "\n" +
          "  Total: " + vOffset + "\n" +
          "Velocity modified:" + "\n" +
          "  X: " + (vec.x + xOffset) + "\n" +
          "  Y: " + (vec.y + yOffset) + "\n" +
          "  Z: " + (vec.z + zOffset) + "\n" +
          "  Total: " + lSpeed + "\n" +
          "Velocity total average: " + speed + "\n" +
          "Velocity total in " + speedType.name() + ": " + speedTypeSpeed + "\n" +
          "Percentage point of visual speedometer: " + v + "\n" +
          "Degree end point: " + (i+45) +"\n" +
          (Config.getVisualSpeedometer()?"Visual Size: ":"Textual display") + Config.getImageSize();

      Color color = new Color(255, 255, 255);

      int y = 0;
      for(String s : debugData.split("\n")){
        drawString(graphics,0, y, s, color.getRGB());
        y+=Minecraft.getInstance().font.lineHeight+1;
      }
    }
  }

  private static void drawString(GuiGraphics graphics, int x, int y, String text, int colorRGB){
    graphics.drawString(
        Minecraft.getInstance().font,
        text,
        x,
        y,
        colorRGB
    );
  }

  private static int getPos(GuiGraphics event, String input, int type) {
    ArrayList<String> passerPose = new ArrayList<>();
    final char[] s = input.toCharArray();
    try{
      for(int i = 0; i <s.length; i++){
        if(s[i] == 'W' || s[i] == 'H'){
          if(type == 0) passerPose.add(String.valueOf(event.guiWidth()));
          else if(type == 1) passerPose.add(String.valueOf(event.guiHeight()));
        }else if(s[i] == 'h' || s[i] == 'w'){
          if(type == 0) passerPose.add(String.valueOf(event.guiWidth() / 2));
          else if(type == 1) passerPose.add(String.valueOf(event.guiHeight() / 2));
        }else if(s[i] == '+'){
          passerPose.add("+");
        }else if(s[i] == '-'){
          passerPose.add("-");
        }else if(s[i] == '*'){
          passerPose.add("/");
        }else if(s[i] == '/'){
          passerPose.add("/");
        }else if(Character.isDigit(s[i])){
          try{
            Integer.parseInt(passerPose.get(i-1));
            passerPose.add(i-1,passerPose.get(i-1)+s[i]);
          }catch (NumberFormatException e){
            passerPose.add(Character.toString(s[i]));
          }
        }else{
          throw new Exception();
        }
      }
    }catch (Exception e){
      passerPose.clear();
      defaultValues(event, type, passerPose);
    }

    //

    int xPos;
    try{
      xPos = Integer.parseInt(passerPose.get(0));
    }catch (NumberFormatException e){
      defaultValues(event, type, passerPose);
      xPos = Integer.parseInt(passerPose.get(0));
    }

    for(int i = 1; i < passerPose.size(); i++){
      boolean first = false;
      String s1 = passerPose.get(i);
      String s2 = "";
      try{
        s2 = passerPose.get(i+1);
      }catch (Exception e){
        first = true;
      }

      if(Objects.equals(s1, "+") && !first){
        xPos += Integer.parseInt(s2);
      }else if(Objects.equals(s1, "-") && !first){
        xPos -= Integer.parseInt(s2);
      }else if(Objects.equals(s1, "*") && !first){
        xPos *= Integer.parseInt(s2);
      }else if(Objects.equals(s1, "/") && !first){
        xPos /= Integer.parseInt(s2);
      }
    }
    if((Config.isDebug()) && Config.getCounter() < 2) {
      LOGGER.info("Selected speed type: "+SpeedTypes.getName(Config.getSpeedType()).getString()+"\n"+
          Arrays.toString(passerPose.toArray())+"\n\n"+
          xPos+"\n\n"+
          (type==0?Config.getXPosition():Config.getYPosition()));
      Config.addCounter();
    }
    return xPos;
  }

  private static void defaultValues(GuiGraphics event, int type, ArrayList<String> passerPose) {
    if(type == 0){
      passerPose.add(String.valueOf(event.guiWidth()));
      passerPose.add("-");
      passerPose.add("3");
    }else if(type == 1){
      passerPose.add(String.valueOf(event.guiHeight()));
      passerPose.add("-");
      passerPose.add("3");
    }
  }
}
