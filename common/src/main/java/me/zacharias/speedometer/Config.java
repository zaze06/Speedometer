package me.zacharias.speedometer;

import dev.architectury.platform.Platform;
import org.json.JSONObject;

import java.io.*;

import me.shedaniel.math.Color;

import static me.zacharias.speedometer.Speedometer.MOD_ID;

public class Config {
  private static JSONObject Config;
  public static final float configVersion = 2.1f;

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

      defualt();
    }else {
      try {
        BufferedReader in = new BufferedReader(new FileReader(config));
        String tmp;
        StringBuilder builder = new StringBuilder();
        while((tmp = in.readLine()) != null){
          builder.append(tmp);
        }
        Config = new JSONObject(builder.toString());
        if(Config.has("version")){
          if(Config.getFloat("version")!=configVersion){
            if(Config.getFloat("version") > configVersion){
              defualt();

              save();
            }else if(Config.getFloat("version") < configVersion){
              Config = new JSONObject();

              defualt();
              save();
            }
          }
        }else{
          Config = new JSONObject();
          defualt();
          save();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static void defualt() {
    if(!Config.has("speed")) {
      Config.put("speed", SpeedTypes.BlockPS);
    }
    if(!Config.has("useKnot")) {
      Config.put("useKnot", false);
    }
    if(!Config.has("color")) {
      Config.put("color", new JSONObject()
          .put("r", 16)
          .put("g", 146)
          .put("b", 158)
      );
    }
    if(!Config.has("visualSpeedometer")) {
      Config.put("visualSpeedometer", false);
    }
    if(!Config.has("xPositionVisual")) {
      Config.put("xPositionVisual", "W-3");
    }
    if(!Config.has("yPositionVisual")) {
      Config.put("yPositionVisual", "H-3");
    }
    if(!Config.has("xPositionText")) {
      Config.put("xPositionText", "W-70");
    }
    if(!Config.has("yPositionText")) {
      Config.put("yPositionText", "H-15");
    }

    if(!Config.has("debug")) {
      Config.put("debug", false);
    }

    if(!Config.has("imagSize")) {
      Config.put("imageSize", 19);
    }

    if(!Config.has("version")) {
      Config.put("version", configVersion);
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

  public static boolean isDebug() {
    if(Config.has("debug")){
      return Config.getBoolean("debug");
    }else{
      return false;
    }
  }

  public static boolean getVisualSpeedometer(){
    if(Config.has("visualSpeedometer")){
      return Config.getBoolean("visualSpeedometer");
    }else {
      return false;
    }
  }

  public static String getXPositionVisual(){
    if(Config.has("xPositionVisual")) {
      return Config.getString("xPositionVisual");
    }else{
      return "W-23";
    }
  }

  public static String getYPositionVisual() {
    if (Config.has("yPositionVisual")) {
      return Config.getString("yPositionVisual");
    } else {
      return "H-23";
    }
  }
  public static String getXPositionText(){
    if(Config.has("xPositionText")) {
      return Config.getString("xPositionText");
    }else{
      return "W-70";
    }
  }

  public static String getYPositionText(){
    if(Config.has("yPositionText")) {
      return Config.getString("yPositionText");
    }else{
      return "H-15";
    }
  }

  public static int getImageSize(){
    if(Config.has("imageSize")){
      return Config.getInt("imageSize");
    }else {
      return 19;
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

  public static void setVisualSpeedometer(boolean visualSpeedometer){
    Config.put("visualSpeedometer", visualSpeedometer);
  }

  public static void setXPositionVisual(String xPositionVisual){
    Config.put("xPositionVisual", xPositionVisual);
  }

  public static void setYPositionVisual(String yPositionVisual){
    Config.put("yPositionVisual", yPositionVisual);
  }

  public static void setXPositionText(String xPositionText){
    Config.put("xPositionText", xPositionText);
  }

  public static void setYPositionText(String yPositionText){
    Config.put("yPositionText", yPositionText);
  }

  public static void setDebug(boolean debug){
    Config.put("debug", debug);
  }

  public static void setImageSize(int imageSize){
    Config.put("imageSize", imageSize);
  }
}
