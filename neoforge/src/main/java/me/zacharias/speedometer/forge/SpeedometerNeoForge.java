package me.zacharias.speedometer.forge;

import com.mojang.datafixers.util.Unit;
import me.zacharias.speedometer.Speedometer;
import net.minecraft.resources.Identifier;
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
  }
}

@EventBusSubscriber(modid = MOD_ID,/* bus = EventBusSubscriber.Bus.MOD, */value = Dist.CLIENT)
class EventHandler
{

  @SubscribeEvent
  private static void clientStart(FMLClientSetupEvent event) {
  }
}
