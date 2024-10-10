package me.zacharias.speedometer;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static me.zacharias.speedometer.Speedometer.*;

public class Config {
  private static JSONObject config;
  public static final float configVersion = 4f;
  private static int counter = 0;
  private static String configPath;
  private static BufferedImage speedometer;
  private static boolean disableVisualSpeedometer = false;

  public static void initialize(){
    if(config != null) throw new RuntimeException("Already Initialized");
    configPath = Platform.getConfigFolder().toString()+"/"+MOD_ID+"/config.json";
    File configFile = new File(configPath);
    if(!configFile.exists()){
      try {
        configFile.getParentFile().mkdir();
        configFile.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      config = new JSONObject();

      defualt();
    }else {
      try {
        BufferedReader in = new BufferedReader(new FileReader(configFile));
        String tmp;
        StringBuilder builder = new StringBuilder();
        while((tmp = in.readLine()) != null){
          builder.append(tmp);
        }

        try{
          config = new JSONObject(builder.toString());
        }catch (JSONException e){
          File dump = new File(Platform.getConfigFolder().toString()+"/"+MOD_ID+"/config-dump_"+formatMillisToDHMS(System.currentTimeMillis())+"_.json");
          LOGGER.error("Config is reset due to invalid content, dumping old content to {}", dump.getPath());
          if(!dump.exists()){
            dump.createNewFile();
          }

          try{
            BufferedWriter out = new BufferedWriter(new FileWriter(dump));
            out.write(builder.toString());
            out.close();
          }catch (IOException ex)
          {
            LOGGER.error("Failed to create a dump file and write to it.", ex);
            LOGGER.warn("Dump content: \n{{}}", builder.toString());
          }

          config = new JSONObject();
          defualt();
          return;
        }

        LOGGER.info("Loaded config successfully");

        if(config.has("version")){
          if(config.getFloat("version")!=configVersion){
            if(config.getFloat("version") > configVersion){
              LOGGER.warn("Config version is too new, resting");
              defualt();

              save();
            }else if(config.getFloat("version") < configVersion){
              config = new JSONObject();
              LOGGER.warn("Config version is outdated, resting");

              defualt();
              save();
            }
          }
        }else{
          config = new JSONObject();
          defualt();
          save();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static void defualt() {
    if(!config.has("speed")) {
      config.put("speed", SpeedTypes.BlockPS);
    }
    if(!config.has("useKnot")) {
      config.put("useKnot", false);
    }
    if(!config.has("color")) {
      config.put("color", new JSONObject()
          .put("r", 16)
          .put("g", 146)
          .put("b", 158)
      );
    }
    if(!config.has("visualSpeedometer")) {
      config.put("visualSpeedometer", false);
    }

    if(!config.has("xPosition")) {
      config.put("xPosition", "W-3");
    }
    if(!config.has("yPosition")) {
      config.put("yPosition", "H-3");
    }

    if(!config.has("debug")) {
      config.put("debug", false);
    }

    if(!config.has("imagSize")) {
      config.put("imageSize", 19);
    }

    if(!config.has("version")) {
      config.put("version", configVersion);
    }

    if(!config.has("showVisualSpeedType")){
      config.put("showVisualSpeedType", false);
    }

    if(!config.has("showSpeedType")){
      config.put("showSpeedType", false);
    }

    if(!config.has("overrideColor")) {
      config.put("overrideColor", false);
    }
  }

  public static void save(){
    File config = new File(configPath);
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
      out.write(Config.config.toString(4));
      out.flush();
      out.close();
    }catch (Exception e){
      throw new RuntimeException(e);
    }
    counter=0;
  }
  
  //region Config Getters
  public static SpeedTypes getSpeedType(){
    if(config.has("speed")){
      return config.getEnum(SpeedTypes.class, "speed");
    }else{
      return SpeedTypes.BlockPS;
    }
  }

  public static boolean getUseKnot() {
    if(config.has("useKnot")){
      return config.getBoolean("useKnot");
    }else{
      return false;
    }
  }

  public static Color getColor(){
    if(config.has("color")){
      JSONObject color = config.getJSONObject("color");
      return new Color(
          color.getInt("r"),
          color.getInt("g"),
          color.getInt("b")
      );
    }else{
      return new Color(16, 146, 158);
    }
  }

  public static int getColorRGB()
  {
    return getColor().getRGB() & 0xFFFFFF;
  }

  public static boolean isDebug() {
    if(config.has("debug")){
      return config.getBoolean("debug");
    }else{
      return false;
    }
  }

  public static boolean getVisualSpeedometer(){
    if(config.has("visualSpeedometer")){
      return config.getBoolean("visualSpeedometer");
    }
    else
    {
      return false;
    }
  }

  public static int getCounter(){
    return counter;
  }
  public static void addCounter(){
    counter++;
  }

  public static String getYPosition(){
    if(config.has("yPosition")) {
      return config.getString("yPosition");
    }else{
      return "H-3";
    }
  }

  public static String getXPosition(){
    if(config.has("xPosition")) {
      return config.getString("xPosition");
    }else{
      return "W-3";
    }
  }

  public static int getImageSize(){
    if(config.has("imageSize")){
      return config.getInt("imageSize");
    }else {
      return 19;
    }
  }

  public static boolean getShowSpeedType(){
    if(config.has("getShowSpeedType")){
      return config.getBoolean("showSpeedType");
    }else{
      return true;
    }
  }
  
  public static String getConfigPath()
  {
    return configPath;
  }
  
  public static BufferedImage getSpeedometer() {
    return speedometer;
  }
  
  public static boolean isDisableVisualSpeedometer() {
    return disableVisualSpeedometer;
  }

  public static boolean isOverrideColor() {
    if(config.has("overrideColor")){
      return config.getBoolean("overrideColor");
    } else {
      return false;
    }
  }
  
  //endregion
  
  //region Config Setters

  public static void setColor(int r, int g, int b){
    config.put("color", new JSONObject()
        .put("r", r)
        .put("g", g)
        .put("b", b)
    );
  }

  public static void setUseKnot(boolean useKnot){
    config.put("useKnot", useKnot);
  }

  public static void setSpeedType(SpeedTypes speedType) {
    config.put("speed", speedType);
  }

  public static void setVisualSpeedometer(boolean visualSpeedometer){
    config.put("visualSpeedometer", visualSpeedometer);
  }

  public static void setXPosition(String xPosition){
    config.put("xPosition", xPosition);
  }

  public static void setYPosition(String yPosition){
    config.put("yPosition", yPosition);
  }

  public static void setDebug(boolean debug){
    config.put("debug", debug);
  }

  public static void setImageSize(int imageSize){
    config.put("imageSize", imageSize);
  }

  public static void setShowSpeedType(boolean showSpeedType){
    config.put("showSpeedType", showSpeedType);
  }
  
  public static void setSpeedometer(BufferedImage speedometer) {
    Config.speedometer = speedometer;
  }
  
  public static void setDisableVisualSpeedometer (boolean disableVisualSpeedometer){
    Config.disableVisualSpeedometer = disableVisualSpeedometer;
  }

  public static void setOverrideColor (boolean overrideColor)
  {
    config.put("overrideColor", overrideColor);
  }
  //endregion
}
