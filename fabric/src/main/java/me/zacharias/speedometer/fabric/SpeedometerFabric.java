package me.zacharias.speedometer.fabric;

import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class SpeedometerFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    Speedometer.init();
  }
}