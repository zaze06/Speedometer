package me.zacharias.speedometer;

import dev.architectury.platform.Platform;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3i;
import org.json.JSONObject;

import javax.swing.plaf.ColorUIResource;
import java.io.*;

import me.shedaniel.math.Color;

import static me.zacharias.speedometer.Speedometer.MOD_ID;

public class Config {
    private static JSONObject Config;

    public static void initialize(){
        if(Config != null) throw new RuntimeException("Already Initialized");
        File config = new File(Platform.getConfigFolder().toString()+"/"+MOD_ID+"/config.json");
        if(!config.exists()){
            try {
                config.getParentFile().mkdir();
                config.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Config = new JSONObject();


            Config.put("speed", SpeedTypes.BlockPS);
            Config.put("useKnot", false);
            Config.put("color", new JSONObject()
                    .put("r", 16)
                    .put("g", 146)
                    .put("b", 158)
            );
            Config.put("debug", false);
        }else {
            try {
                BufferedReader in = new BufferedReader(new FileReader(config));
                String tmp = "";
                StringBuilder builder = new StringBuilder();
                while((tmp = in.readLine()) != null){
                    builder.append(tmp);
                }
                Config = new JSONObject(builder.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void save(){
        File config = new File(Platform.getConfigFolder().toString()+"/"+MOD_ID+"/config.json");
        if(!config.exists()){
            try {
                config.getParentFile().mkdir();
                config.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(config));
            out.write(Config.toString(4));
            out.flush();
            out.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static SpeedTypes getSpeedType(){
        if(Config.has("speed")){
            return Config.getEnum(SpeedTypes.class, "speed");
        }else{
            return SpeedTypes.BlockPS;
        }
    }

    public static boolean getUseKnot() {
        if(Config.has("useKnot")){
            return Config.getBoolean("useKnot");
        }else{
            return false;
        }
    }

    public static Color getColor(){
        if(Config.has("color")){
            JSONObject color = Config.getJSONObject("color");
            return Color.ofRGB(
                    color.getInt("r"),
                    color.getInt("g"),
                    color.getInt("b")
            );
        }else{
            return Color.ofRGB(16, 146, 158);
        }
    }

    public static boolean getIsDebug() {
        if(Config.has("debug")){
            return Config.getBoolean("debug");
        }else{
            return false;
        }
    }

    public static void setColor(Color color){
        Config.put("color", new JSONObject()
                .put("r", color.getRed())
                .put("g", color.getGreen())
                .put("b", color.getBlue())
        );
    }

    public static void setUseKnot(boolean useKnot){
        Config.put("useKnot", useKnot);
    }

    public static void setSpeedType(SpeedTypes speedType) {
        Config.put("speed", speedType);
    }
}
