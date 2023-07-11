package me.zacharias.speedometer.forge;

import dev.architectury.platform.forge.EventBuses;
import me.zacharias.speedometer.Speedometer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Speedometer.MOD_ID)
public class SpeedometerForge {
  public SpeedometerForge() {
    // Submit our event bus to let architectury register our content on the right time
    EventBuses.registerModEventBus(Speedometer.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    Speedometer.init();
  }
}