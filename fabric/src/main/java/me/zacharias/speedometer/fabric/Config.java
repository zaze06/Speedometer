package me.zacharias.speedometer.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.zacharias.speedometer.SpeedTypes;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static me.zacharias.speedometer.Speedometer.LOGGER;

public class Config implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return parent -> {
                ConfigBuilder builder = Speedometer.getConfig(parent);

                return builder.build();
            };
        }
    }