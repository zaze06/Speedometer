package me.zacharias.speedometer.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class SpeedometerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Speedometer.init();

    }
}