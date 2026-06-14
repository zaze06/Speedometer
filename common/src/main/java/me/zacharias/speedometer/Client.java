package me.zacharias.speedometer;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.platform.client.ConfigurationScreenRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static me.zacharias.speedometer.Speedometer.*;

public class Client {
    public static final KeyMapping.Category SPEEDOMETER_KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "name"));
    public static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.speedometer.configKey",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_O,
            SPEEDOMETER_KEY_CATEGORY
    );
    public static final KeyMapping DEBUG_KEY = new KeyMapping(
            "key.speedometer.debugKey",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F6,
            SPEEDOMETER_KEY_CATEGORY
    );

    private static final ArrayList<Double> speeds = new ArrayList<>();

    //private static Identifier speedometerTexture = Identifier.fromNamespaceAndPath("speedometer", "meter/speedometer.png");


    public static void init(){

        final boolean isClothLoaded = Platform.isModLoaded("cloth_config") || Platform.isModLoaded("cloth-config");

        if(isClothLoaded) {
            ConfigurationScreenRegistry.register(Platform.getMod(MOD_ID), parent -> ConfigMenu.getConfig(parent).build());
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
                    Minecraft.getInstance().player.sendSystemMessage(
                            Component
                                    .translatable("speedometer.error.missing_cloth")
                                    .withColor(new Color(190, 0, 0).getRGB())
                                    .append(Component
                                            .translatable("speedometer.error.missing_cloth.open_config")
                                            .withStyle(ChatFormatting.UNDERLINE)
                                            .withStyle((style) -> style.withClickEvent(new ClickEvent.OpenFile(Config.getConfigPath())))
                                    ));
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

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
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

        if (speeds.size() >= Config.getSpeedAvrageSampleCount()) {
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

        int yPos = getPosImp(graphics, width, Config.getYPosition(), false);
        int xPos = getPosImp(graphics, width, Config.getXPosition(), true);

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

            //ImageHandler.register(Identifier.fromNamespaceAndPath(MOD_ID, "speedometer_icon_tmp"), img);

            //TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            //AbstractTexture texture = textureManager.getTexture(ICON.getBackground());

            //int width1 = texture.getTexture().getWidth(0);
            //int height1 = texture.getTexture().getHeight(0);

            //graphics.pose().pushMatrix();
            //graphics.pose().translate(xPos, yPos);
            //graphics.pose().scale((float) Config.getImageSize() / width1, (float) Config.getImageSize() / height1);
            //graphics.pose().scale(1f);

            //graphics.blit(ICON.getBackground(), xPos, yPos, 0, 0, width1, height1, width1, height1);
            //graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON.getBackground(), -100, -100, width1, height1);

            //graphics.pose().popMatrix();



            /*graphics.(Identifier.fromNamespaceAndPath(MOD_ID, "speedometer_icon_tmp"),
                    xPos - img.getWidth(),
                    yPos - img.getHeight(),
                    0, 0,
                    img.getWidth(), img.getHeight(),
                    img.getWidth(), img.getHeight()
            );*/

        }else {
            // i -> x
            // j -> y
            // k -> color RGB int
            drawString(
                    graphics,
                    xPos - width,
                    yPos - lineHeight,
                    speedString,
                    Config.getColor().getRGB()
            );
        }

        if(Config.isDebug() && !Minecraft.getInstance().debugEntries.isOverlayVisible()){
            /*String debugData = "Speedometer: "+VERSION+"\n"+
                    "(xPos, yPos): (" +xPos+ ", " + yPos + ")\n" +
                    "Velocity raw:" + "\n" +
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
                    (Config.getVisualSpeedometer()?"Visual Size: "+Config.getImageSize():"Textual display") + "\n" +
                    (Config.getVisualSpeedometer()?"Creating visual speedometer: " + (Debugger.avrage40SizingTime) + " ms":"") + "\n" +
                    "Sample Size: " + Config.getSpeedAvrageSampleCount();*/
            String debugData = String.format("""
                    Speedometer: %s
                    (xPos, yPos): (%d,%d)
                    Velocity raw:
                      X: %.4f
                      Y: %.4f
                      Z: %.4f
                    Offsets:
                      X: %.4f
                      Y: %.4f
                      Z: %.4f
                      Total: %.4f
                    Velocity modified:
                      X: %.4f
                      Y: %.4f
                      Z: %.4f
                      Total: %.4f
                    Velocity total average: %.4f
                    Sample Size: %d
                    Velocity total in: %s : %.4f
                    Endpoint position: (%.4f, %.4f)
                    Percentage point of visual speedometer: %.4f
                    Mode: %s
                    Visual speedometer time: %tSS:LLL
                    """, VERSION, xPos, yPos, vec.x, vec.y, vec.z, xOffset, yOffset,
                    zOffset, vOffset, (vec.x + xOffset), (vec.y + yOffset), (vec.z + zOffset),
                    lSpeed, speed, Config.getSpeedAvrageSampleCount(), speedType.name(), speedTypeSpeed,
                    Debugger.x, Debugger.y, Debugger.angle,
                    (Config.getVisualSpeedometer()?"Visual Size: "+Config.getImageSize():"Textual display"),
                    Debugger.avrage40SizingTime);
            Color color = new Color(255, 255, 255);

            int y = 0;
            for(String s : debugData.split("\n")){
                drawString(graphics,0, y, s, color.getRGB());
                y+=Minecraft.getInstance().font.lineHeight+1;
            }
        }
    }

    public void drawLine(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1, y = y1;

        while (true) {
            graphics.fill(x, y, x + 1, y + 1, color);

            if (x == x2 && y == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    private static void drawString(GuiGraphicsExtractor graphics, int x, int y, String text, int colorRGB){
        graphics.text(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                colorRGB
        );
    }

    private static int getPosImp(GuiGraphicsExtractor event, int width, String input, boolean isXPosition){
        input = input.trim();
        input = input
                .replaceAll("(W+)|(H+)", String.valueOf(isXPosition?event.guiWidth():event.guiHeight()))
                .replaceAll("(w+)|(h+)", String.valueOf(isXPosition?event.guiWidth()/2:event.guiHeight()/2))
                .replaceAll("(S+)|(s+)", String.valueOf(width));
        if((Config.isDebug()) && Config.getCounter() < 2) {
            //String speedDisplayType = SpeedTypes.getName(Config.getSpeedType()).getString();
            //String splitRawSpeedPosition = Arrays.toString(passerPose.toArray());
            //String rawSpeedPosition = isXPosition ? Config.getXPosition() : Config.getYPosition();
            LOGGER.info("Selected speed type(DEBUG): {}\n{}\n\n\n", isXPosition, input);
            Config.addCounter();
        }
        return getPos(event, width, input, isXPosition);
    }

    private static int getPos(GuiGraphicsExtractor event, int width, String input, boolean isXPosition) {
        ArrayList<String> tokens = new ArrayList<>();
        final char[] s = input.toCharArray();

        try{
            for(int i = 0; i <s.length; i++){
                char c = s[i];
                if(c == '+' ||
                        c == '-' ||
                        c == '*' ||
                        c == '/'){
                    tokens.add(Character.toString(c));
                }
                else if(Character.isDigit(c)){
                    int lastIndex = tokens.size() - 1;
                    if(lastIndex >= 0 && tokens.get(lastIndex).matches("^[0-9]+$")) {
                        tokens.set(tokens.size() - 1, tokens.getLast() + c);
                    }
                    else
                    {
                        tokens.add(Character.toString(c));
                    }
                }
                else{
                    throw new IllegalArgumentException("Invalid character in input string: " + c);
                }
            }
        }catch (Exception e){
            tokens.clear();
            defaultValues(event, isXPosition, tokens);
            if(Config.getWarnCount() < 4) {
                LOGGER.warn("Failed for reason {} setting default values of (W|H)-3", e.getMessage());
                Config.IncrementWarnCount();
            }
        }

        int position;
        try{
            position = Integer.parseInt(tokens.getFirst());
        }catch (NumberFormatException e){
            tokens.clear();
            defaultValues(event, isXPosition, tokens);
            position = Integer.parseInt(tokens.getFirst());
            if(Config.getWarnCount() < 4) {
                LOGGER.warn("Failed for reason {} setting default values of (W|H)-3", e.getMessage());
                Config.IncrementWarnCount();
            }
        }

        for(int i = 1; i < tokens.size(); i+=2){
            String operator = tokens.get(i);
            if(i + 1 >= tokens.size()) {
                LOGGER.error("Invalid expression: missing operand after operator '{}'", operator);
                break;
            }
            String operand = tokens.get(i + 1);
            int value;
            try {
                value = Integer.parseInt(operand);
            }
            catch (NumberFormatException e) {
                LOGGER.error("Invalid operand: '{}'. Using default value. REPORT THIS! https://github.com/zaze06/Speedometer/issues/new/choose", operand);
                Minecraft.getInstance().emergencySaveAndCrash(new CrashReport("Invalid operand in speedometer position calculation. REPORT THIS! https://github.com/zaze06/Speedometer/issues/new/choose", e));
                return -1;
            }

            switch (operator) {
                case "+" -> position += value;
                case "-" -> position -= value;
                case "*" -> position *= value;
                case "/" -> position /= value;
            }
        }

        if (Config.isDebug() && Config.getCounter() < 2) {
            LOGGER.info("Selected speed type: {}\n{}\n\n{}\n\n{}",
                    SpeedTypes.getName(Config.getSpeedType()).getString(),
                    Arrays.toString(tokens.toArray()),
                    position,
                    isXPosition ? Config.getXPosition() : Config.getYPosition());
            Config.addCounter();
        }

        return position;
    }

    private static void defaultValues(GuiGraphicsExtractor event, boolean isXPosition, ArrayList<String> passerPose) {
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
