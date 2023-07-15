package me.zacharias.speedometer;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;

public class Speedometer
{
  public static final String MOD_ID = "speedometer";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final String VERSION = Platform.getMod(MOD_ID).getVersion();

  public static void init() {
    LOGGER.info("Loading speedometer by Allen");


    if(Platform.getEnvironment() != Env.CLIENT) {
      LOGGER.error("You're running speedometer on somthing other then a client, this is not supported");
      try {
        for (File f : Objects.requireNonNull(Platform.getModsFolder().toFile().listFiles())) {
          if (f.getName().startsWith("speedometer")) {
            String fileName = "speedometer-" + VERSION + ".jar.disable";
            if(f.renameTo(new File(f.getParent(), fileName))){
              LOGGER.warn("Successfully in renaming the mod jar file to "+fileName);
              LOGGER.warn("You should remove the file from "+Platform.getModsFolder().toString());
            }else{
              LOGGER.warn("Unsuccessful in renaming mod jar");
              LOGGER.warn("You should remove the mod from "+Platform.getModsFolder().toString()+" to no longer receive this message");
            }
          }
        }
      }catch (NullPointerException e){
        LOGGER.warn("Can't disable the mod");
      }
      return;
    }

    Client.init();
  }
}
