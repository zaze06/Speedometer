package me.zacharias.speedometer.fabric;

import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.intellij.lang.annotations.Identifier;

import java.awt.*;

public class SpeedometerFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Speedometer.init();
    ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
      @Override
      public void onResourceManagerReload(ResourceManager resourceManager) {
        Speedometer.loadSpeedometers(resourceManager);
      }
      
      @Override
      public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("speedometer", "Loading the visual speedometers");
      }
    });
  }
}