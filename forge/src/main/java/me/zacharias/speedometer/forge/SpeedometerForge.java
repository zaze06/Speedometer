package me.zacharias.speedometer.forge;

import com.mojang.datafixers.util.Unit;
import dev.architectury.platform.forge.EventBuses;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static me.zacharias.speedometer.Speedometer.MOD_ID;

@Mod(MOD_ID)
public class SpeedometerForge {
    public SpeedometerForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Speedometer.init();
    }
}

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
class EventHandler
{
    @SubscribeEvent
    public static void onResourceReload(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(new SimplePreparableReloadListener<Unit>() {
            @Override
            protected @NotNull Unit prepare(@NotNull ResourceManager arg, @NotNull ProfilerFiller arg2) {
                return Unit.INSTANCE;
            }

            @Override
            protected void apply(@NotNull Unit object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller arg2) {
                try {
                    Speedometer.loadSpeedometers(resourceManager);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}