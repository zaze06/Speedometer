package me.zacharias.speedometer;

import com.mojang.datafixers.util.Unit;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

public class SpeedometerReloadListener extends SimplePreparableReloadListener {
    @Override
    protected @NotNull Unit prepare(@NotNull ResourceManager arg, @NotNull ProfilerFiller arg2) {
        return Unit.INSTANCE;
    }

    @Override
    protected void apply(Object preparations, ResourceManager manager, ProfilerFiller profiler) {
        Speedometer.loadSpeedometers(manager);
    }
}
