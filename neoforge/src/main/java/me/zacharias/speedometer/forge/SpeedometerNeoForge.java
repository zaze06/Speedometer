package me.zacharias.speedometer.forge;

import me.zacharias.speedometer.Speedometer;
import net.neoforged.fml.common.Mod;

@Mod(Speedometer.MOD_ID)
public class SpeedometerNeoForge {
  public SpeedometerNeoForge() {
    // Submit our event bus to let architectury register our content on the right time
    Speedometer.init();
  }
}