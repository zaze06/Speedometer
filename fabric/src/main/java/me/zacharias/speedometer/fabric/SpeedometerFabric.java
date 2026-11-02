package me.zacharias.speedometer.fabric;

import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class SpeedometerFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Speedometer.init();
  }
}
