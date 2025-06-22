package me.zacharias.speedometer.forge;

import com.mojang.datafixers.util.Unit;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import org.jetbrains.annotations.NotNull;

import static me.zacharias.speedometer.Speedometer.MOD_ID;

@Mod(MOD_ID)
public class SpeedometerNeoForge {
  public SpeedometerNeoForge(IEventBus eventBus) {
    // Submit our event bus to let architectury register our content on the right time
    Speedometer.init();

    //ResourcePackLoader.buildPackFinder()
  }
}

@EventBusSubscriber(modid = MOD_ID,/* bus = EventBusSubscriber.Bus.MOD, */value = Dist.CLIENT)
class EventHandler
{
  /**
   * Register the reload listener for the speedometers
   * This is required since i haven't found how to put this in the Architecture Abstraction layer(Common module)
   * TODO: Find a way to put this in the Abstraction layer
   * @param event The event that is fired when the client reloads resources
   */
  @SubscribeEvent
  private static void onResourceReload(AddClientReloadListenersEvent event) {
    event.addListener(ResourceLocation.fromNamespaceAndPath(MOD_ID, "reload_listener"), new SimplePreparableReloadListener<Unit>() {
      @Override
      protected @NotNull Unit prepare(@NotNull ResourceManager arg, @NotNull ProfilerFiller arg2) {
        return Unit.INSTANCE;
      }
      
      @Override
      protected void apply(@NotNull Unit object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller arg2) {
        Speedometer.loadSpeedometers(resourceManager);
      }
    });
  }

  @SubscribeEvent
  private static void clientStart(FMLClientSetupEvent event) {
    //Minecraft.getInstance().getResourcePackRepository().addPackFinder(consumer -> consumer.accept());
  }
}