package me.zacharias.speedometer.forge;

import com.mojang.datafixers.util.Unit;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientNeoForgeMod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static me.zacharias.speedometer.Speedometer.*;

@Mod(Speedometer.MOD_ID)
public class SpeedometerNeoForge {
  public SpeedometerNeoForge(IEventBus eventBus) {
    // Submit our event bus to let architectury register our content on the right time
    Speedometer.init();

    //ResourcePackLoader.buildPackFinder()
  }
}

@EventBusSubscriber(modid = Speedometer.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
class EventHandler
{
  /**
   * Register the reload listener for the speedometers
   * This is required since i havent found how to put this in the Architecture Abstraction layer(Common module)
   * TODO: Find a way to put this in the Abstraction layer
   * @param event The event that is fired when the client reloads resources
   */
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

  @SubscribeEvent
  private static void clientStart(FMLClientSetupEvent event) {
    //Minecraft.getInstance().getResourcePackRepository().addPackFinder(consumer -> consumer.accept());
  }
}