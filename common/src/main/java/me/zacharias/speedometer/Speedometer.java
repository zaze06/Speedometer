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
  public static final String VERSION = "3.2";

  public static void init() {
    LOGGER.info("Loading speedometer by Allen");

    if(Platform.getEnvironment() != Env.CLIENT) {
      LOGGER.error("You're running speedometer on somthing other then a client, this is not supported");
      try {
        for (File f : Objects.requireNonNull(Platform.getModsFolder().toFile().listFiles())) {
          if (f.getName().startsWith("speedometer")) {
            if(f.renameTo(new File(f.getParent(), "speedometer-" + VERSION + ".jar.dis"))){
              LOGGER.warn("Unsuccesful in renaming mod jar");
            }
          }
        }
      }catch (NullPointerException e){
        LOGGER.warn("Can't disable the mod");
      }
      LOGGER.warn("You should remove the mod from "+Platform.getModsFolder().toString()+" to no longer receive this message");
      return;
    }

    Client.init();
  }
}
