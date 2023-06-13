package me.zacharias.speedometer;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.event.EventHandler;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

public class Speedometer
{
	public static final String MOD_ID = "speedometer";
	public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static final KeyMapping CONFIG_KEY = new KeyMapping(
			"speedometer.key.configkey",
			InputConstants.Type.KEYSYM,
			InputConstants.KEY_O,
			"speedometer.key.catagory"
	);

	public static void init() {
		if(Platform.isForge()){
			LOGGER.info("Hello, Forge! from Architectury");
		}else if(Platform.isFabric()){
			LOGGER.info("Hello, Fabric! from Architectury");
		}else{
			LOGGER.info("Hello! from Architectury");
		}

		if(Platform.getEnvironment() != Env.CLIENT) return;

		KeyMappingRegistry.register(CONFIG_KEY);
		ClientTickEvent.CLIENT_POST.register(minecraft -> {
			if(CONFIG_KEY.consumeClick()){
				Minecraft.getInstance().setScreen(getConfig(Minecraft.getInstance().screen).build());
			}
		});

		Config.initialize();
		Config.save();

		// TODO add cloth config for abstract config system

		ArrayList<Double> speeds = new ArrayList<>();

		ClientGuiEvent.RENDER_HUD.register((graphics, tick) -> {
			if(Minecraft.getInstance().player == null) return;
			Entity entity = Minecraft.getInstance().player.getRootVehicle();

			Level world = entity.level();
			double x = entity.position().x;
			double y = entity.position().y;
			double z = entity.position().z;

			Vec3 vec = entity.getDeltaMovement();

			double yOffset = 0.0784000015258789D;
			double xOffset = 0D;
			double zOffset = 0D;
			double vOffset = 0D;

			if (entity instanceof Player e) {
				if (!e.onGround() && e.isCreative()) {
					yOffset = 0;
				} else if (e.isInWater()) {
					yOffset = 0;
				}
			} else if (entity instanceof Boat) {
				yOffset = 0;
			}

			double speed = (Math.sqrt(Math.pow(vec.x + xOffset, 2) + Math.pow(vec.y + yOffset, 2) + Math.pow(vec.z + zOffset, 2)) * 20)+vOffset;

			if (speeds.size() >= 30) {
				speeds.remove(0);
			}
			speeds.add(speed);
			speed = 0;
			for (Double aDouble : speeds) {
				speed += aDouble;
			}
			speed = speed / speeds.size();

			SpeedTypes speedType = Config.getSpeedType();
			if (speedType == SpeedTypes.KNOT || (entity instanceof Boat && Config.getUseKnot())) {
				speed = speed * 1.94384449;
			}else if (speedType == SpeedTypes.KMPH) {
				speed = speed * 3.6;
			} else if (speedType == SpeedTypes.MPH) {
				speed = speed * 2.23693629;
			}

			String format = String.format("%.2f", speed);


			// i -> x
			// j -> y
			// k -> color RGB int
			graphics.drawString(
					Minecraft.getInstance().font,
					format+" "+SpeedTypes.getName(speedType).getString(),
					getPos(graphics, "W-70", 0, false),
					getPos(graphics, "H-17", 1, true),
					Config.getColor().getColor());
		});

	}

	static boolean flag = true;

	private static int getPos(GuiGraphics event, String input, int type, boolean changeFlag) {
		ArrayList<String> paserdPos = new ArrayList<String>();
		final char[] s = input.toCharArray();
		try{
			for(int i = 0; i <s.length; i++){
				if(s[i] == 'W' || s[i] == 'H'){
					if(type == 0) paserdPos.add(event.guiWidth()+"");
					else if(type == 1) paserdPos.add(event.guiHeight()+"");
				}else if(s[i] == 'h' || s[i] == 'w'){
					if(type == 0) paserdPos.add(((int)(event.guiWidth()/2))+"");
					else if(type == 1) paserdPos.add(((int)(event.guiHeight()/2))+"");
				}else if(s[i] == '+'){
					paserdPos.add("+");
				}else if(s[i] == '-'){
					paserdPos.add("-");
				}else if(s[i] == '*'){
					paserdPos.add("/");
				}else if(s[i] == '/'){
					paserdPos.add("/");
				}else if(testIfInt(s[i])){
					try{
						Integer.parseInt(paserdPos.get(i-1));
						paserdPos.add(i-1,paserdPos.get(i-1)+s[i]);
					}catch (NumberFormatException e){
						paserdPos.add(Character.toString(s[i]));
					}
				}else{
					throw new Exception();
				}
			}
		}catch (Exception e){
			paserdPos.clear();
			if(type == 0){
				paserdPos.add(event.guiWidth()+"");
				paserdPos.add("-");
				paserdPos.add("70");
			}else if(type == 1){
				paserdPos.add(event.guiHeight()+"");
				paserdPos.add("-");
				paserdPos.add("15");
			}
		}



		int xPos = 0;
		try{
			xPos = Integer.parseInt(paserdPos.get(0));
		}catch (NumberFormatException e){
			if(type == 0){
				paserdPos.add(event.guiWidth()+"");
				paserdPos.add("-");
				paserdPos.add("70");
			}else if(type == 1){
				paserdPos.add(event.guiHeight()+"");
				paserdPos.add("-");
				paserdPos.add("15");
			}
			xPos = Integer.parseInt(paserdPos.get(0));
		}

		for(int i = 1; i < paserdPos.size(); i++){
			boolean first = i == 0;
			String s1 = paserdPos.get(i);
			String s2 = "";
			try{
				s2 = paserdPos.get(i+1);
			}catch (Exception e){
				first = true;
			}

			if(s1 == "+" && !first){
				xPos += Integer.parseInt(s2);
			}else if(s1 == "-" && !first){
				xPos -= Integer.parseInt(s2);
			}else if(s1 == "*" && !first){
				xPos *= Integer.parseInt(s2);
			}else if(s1 == "/" && !first){
				xPos /= Integer.parseInt(s2);
			}
		}
		if((Platform.isDevelopmentEnvironment() || Config.getIsDebug()) && flag) {
			LOGGER.info("Selected speed type: "+SpeedTypes.getName(Config.getSpeedType()).getString()+"\n"+
					Arrays.toString(paserdPos.toArray())+"\n\n"+
					xPos);
			flag = !changeFlag;
		}
		return xPos;
	}

	private static boolean testIfInt(char c) {
		int i = Integer.parseInt(Character.toString(c));
		return (i == 0 || i == 1 || i == 2 ||
				i == 3 || i == 4 || i == 5 ||
				i == 6 || i == 7 || i == 8 ||
				i == 9);
	}

	public static ConfigBuilder getConfig(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.translatable("speedometer.config.name"));

		ConfigCategory category = builder.getOrCreateCategory(Component.translatable("speedometer.config.category.name"));

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		category.addEntry(entryBuilder.startEnumSelector(Component.translatable("speedometer.config.speed"), SpeedTypes.class, me.zacharias.speedometer.Config.getSpeedType())
				.setEnumNameProvider(SpeedTypes::getName)
				.setSaveConsumer(me.zacharias.speedometer.Config::setSpeedType)
				.build()
		);

		category.addEntry(entryBuilder.startColorField(Component.translatable("speedometer.config.color"), me.zacharias.speedometer.Config.getColor())
				.setSaveConsumer2(me.zacharias.speedometer.Config::setColor)
				.build());

		category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.knot"), me.zacharias.speedometer.Config.getUseKnot())
				.setSaveConsumer(me.zacharias.speedometer.Config::setUseKnot)
				.build()
		);

		builder.setSavingRunnable(me.zacharias.speedometer.Config::save);

		return builder;
	}
}