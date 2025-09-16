package me.zacharias.speedometer;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static me.zacharias.speedometer.Speedometer.LOGGER;

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

    /**
     * Rotates a BufferedImage around a specific point.
     *
     * @param image The original BufferedImage to rotate.
     * @param angle The angle to rotate in degrees (double precision).
     * @param x The x coordinate of the point to rotate around (int precision).
     * @param y The y coordinate of the point to rotate around (int precision).
     * @return A new BufferedImage containing the rotated image.
     */
    public static BufferedImage rotateImage(BufferedImage image, double angle, int x, int y) {
        // Convert the angle from degrees to radians
        double radians = Math.toRadians(angle);

        // Get image dimensions
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new BufferedImage with the same width and height
        BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());

        // Create a Graphics2D object from the new image
        Graphics2D g2d = rotatedImage.createGraphics();

        // Perform the rotation around the specified point (x, y)
        AffineTransform transform = new AffineTransform();
        // Translate the rotation point to the origin (0, 0)
        transform.translate(x, y);
        // Rotate around the origin
        transform.rotate(radians);
        // Translate back to the original position
        transform.translate(-x, -y);

        // Draw the rotated image
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    public static void register(ResourceLocation location, BufferedImage img) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            DynamicTexture dt = new DynamicTexture(NativeImage.read(out.toByteArray()));
            Minecraft.getInstance().getTextureManager().register(location, dt);
            out.close();
        }catch (Exception ex)
        {
            LOGGER.error("Failed to register image for " + location.toString());
            LOGGER.error(ex.getMessage());
            Config.setDisableVisualSpeedometer(true);
        }
    }
}