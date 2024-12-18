package me.zacharias.speedometer.fabric;

import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class SpeedometerFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Speedometer.init();

    //Minecraft.getInstance().getResourcePackRepository().addPack()

    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
      /**
       * Register the reload listener for the speedometers
       * This is required since i haven't found how to put this in the Architecture Abstraction layer(Common module)
       * TODO: Find a way to put this in the Abstraction layer
       * @param resourceManager The event that is fired when the client reloads resources
       */
      @Override
      public void onResourceManagerReload(ResourceManager resourceManager) {
        Speedometer.loadSpeedometers(resourceManager);
      }
      
      @Override
      public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("speedometer", "visual_speedometer_reload_listener");
      }
    });
  }
}