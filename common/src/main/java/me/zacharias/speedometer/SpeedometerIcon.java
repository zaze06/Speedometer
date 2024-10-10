package me.zacharias.speedometer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector2i;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import static me.zacharias.speedometer.Speedometer.MOD_ID;

public class SpeedometerIcon {
    private BufferedImage speedometerIcon;
    private Pointer pointer;
    private int start;
    private int end;
    private float scale;
    private int max;
    private boolean overflow;
    private boolean g = false;
    
    public SpeedometerIcon(JSONObject config, ResourceManager resourceManager) throws MissingPropertyException, IOException, JSONException
    {
        if(!config.has("background")) throw new MissingPropertyException("background");
        
        String background = config.getString("background");
        
        if(background.contains(":"))
        {
            background = background.replaceFirst(":", ":textures/");
        }
        else
        {
            background = "textures/"+background;
        }
        
        Optional<Resource> speedometerIcon = resourceManager.getResource(ResourceLocation.read(background).getOrThrow(s -> new MissingPropertyException("background")));
        if(speedometerIcon.isEmpty()) throw new MissingPropertyException("background");
        
        InputStream stream = speedometerIcon.get().open();
        this.speedometerIcon = ImageIO.read(stream);
        stream.close();
        
        if(!config.has("start")) throw new MissingPropertyException("start");
        this.start = config.getInt("start");
        
        if(!config.has("end")) throw new MissingPropertyException("end");
        this.end = config.getInt("end");
        
        if(!config.has("scale")) throw new MissingPropertyException("scale");
        this.scale = config.getFloat("scale");
        
        if(!config.has("pointer")) throw new MissingPropertyException("pointer");
        this.pointer = new Pointer(config.getJSONObject("pointer"), resourceManager, new Vector2i(this.speedometerIcon.getWidth(), this.speedometerIcon.getHeight()));
        
        if(!config.has("maxSpeed")) throw new MissingPropertyException("maxSpeed");
        this.max = config.getInt("maxSpeed");
        
        if(!config.has("overflow")) throw new MissingPropertyException("overflow");
        this.overflow = config.getBoolean("overflow");
    }
    
    public BufferedImage getSpeedometerIcon(double speed)
    {
        BufferedImage img = ImageHandler.clone(speedometerIcon);
        Graphics2D graphics = img.createGraphics();
        pointer.draw(graphics, start, end, max, overflow, Math.pow(speed, scale));
        return img;
    }
}

class Pointer
{
    private BufferedImage image;
    private Color color;
    private Vector2i start;
    private int length;
    private boolean g = false;
    
    public Pointer(JSONObject pointer, ResourceManager resourceManager, Vector2i size) throws MissingPropertyException, IOException, JSONException
    {
        if(!pointer.has("start")) throw new MissingPropertyException("pointer/start");
        if(pointer.get("start") instanceof JSONObject jsonObject)
        {
            if(!jsonObject.has("x")) throw new MissingPropertyException("pointer/start/x");
            if(!jsonObject.has("y")) throw new MissingPropertyException("pointer/start/y");
            start = new Vector2i(jsonObject.getInt("x"), jsonObject.getInt("y"));
        }
        else if(pointer.get("start") instanceof String str)
        {
            if(str.isEmpty()) throw new MissingPropertyException("pointer/start");
            
            if(str.matches("^\\([0-9]+,( )?[0-9]+\\)+$"))
            {
                String[] split = str.split(",");
                start = new Vector2i(Integer.parseInt(split[0].substring(1)), Integer.parseInt(split[1].substring(0, split[1].length()-1)));
            }
            else if(str.equalsIgnoreCase("center"))
            {
                start = new Vector2i(size.x / 2, size.y / 2);
            }
            else throw new MissingPropertyException("pointer/start");
            
        }
        
        if(pointer.has("image"))
        {
            String imageResourceLocation = pointer.getString("image");
            
            if(imageResourceLocation.contains(":"))
            {
                imageResourceLocation = imageResourceLocation.replaceFirst(":", ":textures/");
            }
            else
            {
                imageResourceLocation = "textures/"+imageResourceLocation;
            }
            
            Optional<Resource> image = resourceManager.getResource(ResourceLocation.read(imageResourceLocation).getOrThrow(s -> new MissingPropertyException("pointer/image")));
            if(image.isEmpty()) throw new MissingPropertyException("pointer/image");
            
            InputStream stream = image.get().open();
            this.image = ImageHandler.scale(ImageIO.read(stream), size.x, size.y);
            stream.close();
        }
        else if(pointer.has("length"))
        {
            if(pointer.get("length") instanceof String str)
            {
                length = switch (str.toLowerCase())
                {
                    case "half" -> size.x / 2;
                    case "full" -> size.x;
                    default -> throw new MissingPropertyException("pointer/length");
                };
            }
            else if(pointer.get("length") instanceof Integer integer)
            {
                length = integer;
            }
            else throw new MissingPropertyException("pointer/length");

            if(pointer.has("color"))
            {
                String c = pointer.getString("color");
                if(!c.matches("^#[0-9a-fA-F]{6}$")) throw new MissingPropertyException("pointer/color");
                color = new Color(Integer.parseInt(c.substring(1), 16));
            }
            else throw new MissingPropertyException("pointer/color");
        }
        else throw new MissingPropertyException("pointer/image or pointer/length");
    }
    
    public void draw(Graphics2D g2d, int start, int end, int max, boolean overflow, double speed)
    {
        Color c = color;
        if(Config.isOverrideColor())
        {
            c = Config.getColor();
        }
        double angle = ((speed/max) * end)+start;
        if(angle > end && !overflow) angle = end;
        Debuger.angle = angle;
        
        if(Objects.nonNull(image))
        {
            int centerX = this.start.x;
            int centerY = this.start.y;
            BufferedImage image = ImageHandler.rotateImage(this.image, angle, centerX, centerY);
            g2d.drawImage(image, 0, 0, null);
        }
        else if(c != null && length > 0)
        {
            double angleRads = Math.toRadians(180+angle);
            int endX = (int) (Math.cos(angleRads) * length + this.start.x);
            int endY = (int) (Math.sin(angleRads) * length + this.start.y);
            Debuger.x = endX;
            Debuger.y = endY;

            g2d.setColor(c);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(this.start.x, this.start.y, endX, endY);
        }
        else
        {
            Config.setDisableVisualSpeedometer(true);
            throw new NullPointerException("image and line pointer both are undefined");
        }
    }
}
