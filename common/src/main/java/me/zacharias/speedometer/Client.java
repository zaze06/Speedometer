package me.zacharias.speedometer;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static me.zacharias.speedometer.Speedometer.*;

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

  private static final ArrayList<Double> speeds = new ArrayList<>();


  public static void init(){
    
    final boolean isClothLoaded = Platform.isModLoaded("cloth_config") || Platform.isModLoaded("cloth-config");
    
    if(isClothLoaded) {
      Platform.getMod(MOD_ID).registerConfigurationScreen(parent -> ConfigMenu.getConfig(parent).build());
    }
    else
    {
      LOGGER.warn("Missing Cloth Config API, In game config menu will not be available");
    }
    
    KeyMappingRegistry.register(CONFIG_KEY);
    ClientTickEvent.CLIENT_POST.register(minecraft -> {
      if(CONFIG_KEY.consumeClick()){
        if(isClothLoaded) {
          Minecraft.getInstance().setScreen(ConfigMenu.getConfig(Minecraft.getInstance().screen).build());
        }
        else if(Minecraft.getInstance().player != null)
        {
            Minecraft.getInstance().player.displayClientMessage(
                Component
                    .translatable("speedometer.error.missing_cloth")
                    .withColor(new Color(190, 0, 0).getRGB())
                    .append(" ")
                    .append(Component
                        .literal("Open Config")
                        .withStyle(ChatFormatting.UNDERLINE)
                        .withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, Config.getConfigPath())))
                    ), false);
          LOGGER.warn(Component.translatable("speedometer.error.missing_cloth").getString());
        }
        else
        {
          LOGGER.warn(Component.translatable("speedometer.error.missing_cloth").getString());
        }
      }
    });

    KeyMappingRegistry.register(DEBUG_KEY);
    ClientTickEvent.CLIENT_POST.register(minecraft -> {
      if(DEBUG_KEY.consumeClick()){
        Config.setDebug(!Config.isDebug());
      }
    });

    Config.initialize();
    Config.save();

    ClientGuiEvent.RENDER_HUD.register(Client::render);

    LOGGER.info("Finished loading speedometer");
  }

  private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
    if(Minecraft.getInstance().player == null) return;
    if(Minecraft.getInstance().options.hideGui) return;
    Entity entity = Minecraft.getInstance().player.getRootVehicle();

    Vec3 vec = new Vec3(
        entity.getX() - entity.xOld,
        entity.getY() - entity.yOld,
        entity.getZ() - entity.zOld
    );

    double yOffset = 0D;
    double xOffset = 0D;
    double zOffset = 0D;
    double vOffset = 0D;

    double speed = (Math.sqrt(Math.pow(vec.x + xOffset, 2) + Math.pow(vec.y + yOffset, 2) + Math.pow(vec.z + zOffset, 2)) * 20)+vOffset;
    double lSpeed = speed;

    if (speeds.size() >= 30) {
      speeds.removeFirst();
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
    
    String speedString = format + " " + SpeedTypes.getName(speedType).getString();
    
    int width = switch ((Config.getVisualSpeedometer() && !Config.isDisableVisualSpeedometer()) ? 1 : 0){
      case 1 -> Config.getImageSize();
      case 0 -> Minecraft.getInstance().font.width(speedString);
      default -> 0;
    };

    int yPos = getPos(graphics, width, Config.getYPosition(), false);
    int xPos = getPos(graphics, width, Config.getXPosition(), true);

    int lineHeight = Minecraft.getInstance().font.lineHeight;

    if(Config.getVisualSpeedometer() && !Config.isDisableVisualSpeedometer()){

      //double v = speedTypeSpeed / speedType.gatMaxVisual();

      BufferedImage img = ImageHandler.scale(ICON.getSpeedometerIcon(speedTypeSpeed), Config.getImageSize(), Config.getImageSize());

      for(int x1 = 0; x1 < img.getWidth(); x1++){
        for(int y1 = 0; y1 < img.getHeight(); y1++){
          int x2 = x1 + xPos - img.getWidth();
          int y2 = y1 + yPos - img.getHeight();
          
          int rgb = img.getRGB(x1, y1);
          
          if(new Color(rgb).equals(Color.black)) continue;
          
          graphics.fill(x2, y2, x2+1, y2+1, rgb);
        }
      }

    }else {
      // i -> x
      // j -> y
      // k -> color RGB int
      graphics.drawString(
          Minecraft.getInstance().font,
          speedString,
          xPos - width,
          yPos - lineHeight,
          Config.getColor().getRGB()
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
          "Endpoint position: (" + Debugger.x + ", " + Debugger.y + ")\n" +
          "Percentage point of visual speedometer: " + Debugger.angle + "\n" +
          (Config.getVisualSpeedometer()?"Visual Size: "+Config.getImageSize():"Textual display");

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

  private static int getPos(GuiGraphics event, int width, String input, boolean isXPosition) {
    ArrayList<String> passerPose = new ArrayList<>();
    final char[] s = input.toCharArray();
    
    try{
      for(int i = 0; i <s.length; i++){
        char c = s[i];
        if(c == 'W' || c == 'H'){
          if(isXPosition) passerPose.add(String.valueOf(event.guiWidth()));
          else passerPose.add(String.valueOf(event.guiHeight()));
        }
        else if(c == 'h' || c == 'w'){
          if(isXPosition) passerPose.add(String.valueOf(event.guiWidth() / 2));
          else passerPose.add(String.valueOf(event.guiHeight() / 2));
        }
        else if(c == 'S' || c == 's'){
          passerPose.add(String.valueOf(width));
        }
        else if(c == '+' ||
                c == '-' ||
                c == '*' ||
                c == '/'){
          passerPose.add(Character.toString(c));
        }
        else if(Character.isDigit(c)){
          int lastIndex = i - 1;
          if(lastIndex > 0 && passerPose.get(lastIndex).matches("^[0-9]+$")) {
            passerPose.add(passerPose.removeLast() + c);
          }
          else
          {
            passerPose.add(Character.toString(c));
          }
        }
        else{
          throw new Exception();
        }
      }
    }catch (Exception e){
      passerPose.clear();
      defaultValues(event, isXPosition, passerPose);
    }

    //

    int position;
    try{
      position = Integer.parseInt(passerPose.getFirst());
    }catch (NumberFormatException e){
      passerPose.clear();
      defaultValues(event, isXPosition, passerPose);
      position = Integer.parseInt(passerPose.getFirst());
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
        position += Integer.parseInt(s2);
      }else if(Objects.equals(s1, "-") && !first){
        position -= Integer.parseInt(s2);
      }else if(Objects.equals(s1, "*") && !first){
        position *= Integer.parseInt(s2);
      }else if(Objects.equals(s1, "/") && !first){
        position /= Integer.parseInt(s2);
      }
    }
    if((Config.isDebug()) && Config.getCounter() < 2) {
      String speedDisplayType = SpeedTypes.getName(Config.getSpeedType()).getString();
      String splitRawSpeedPosition = Arrays.toString(passerPose.toArray());
      String rawSpeedPosition = isXPosition ? Config.getXPosition() : Config.getYPosition();
      LOGGER.info("Selected speed type: {}\n{}\n\n{}\n\n{}", speedDisplayType, splitRawSpeedPosition, position, rawSpeedPosition);
      Config.addCounter();
    }
    return position;
  }

  private static void defaultValues(GuiGraphics event, boolean isXPosition, ArrayList<String> passerPose) {
    if(isXPosition)
    {
      passerPose.add(String.valueOf(event.guiWidth()));
      passerPose.add("-");
      passerPose.add("3");
    }
    else
    {
      passerPose.add(String.valueOf(event.guiHeight()));
      passerPose.add("-");
      passerPose.add("3");
    }
  }
}
