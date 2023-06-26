package me.zacharias.speedometer.fabric;

import me.zacharias.speedometer.Speedometer;
import net.fabricmc.api.ModInitializer;

public class SpeedometerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Speedometer.init();
    }
}