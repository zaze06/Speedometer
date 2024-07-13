package me.zacharias.speedometer.forge;

import com.mojang.datafixers.util.Unit;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ClientNeoForgeMod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod(Speedometer.MOD_ID)
public class SpeedometerNeoForge {
  public SpeedometerNeoForge() {
    // Submit our event bus to let architectury register our content on the right time
    Speedometer.init();
  }
}

@EventBusSubscriber(modid = Speedometer.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
class stuff
{
  @SubscribeEvent
  private static void onResourceReload(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(new SimplePreparableReloadListener<Unit>() {
      @Override
      protected Unit prepare(ResourceManager arg, ProfilerFiller arg2) {
        return Unit.INSTANCE;
      }
      
      @Override
      protected void apply(Unit object, ResourceManager resourceManager, ProfilerFiller arg2) {
        Speedometer.loadSpeedometers(resourceManager);
      }
    });
  }
}