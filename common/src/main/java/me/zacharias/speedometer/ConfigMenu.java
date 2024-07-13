package me.zacharias.speedometer;

import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class ConfigMenu {
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

    category.addEntry(entryBuilder.startColorField(Component.translatable("speedometer.config.color"), me.zacharias.speedometer.Config.getColor().getRGB())
        .setSaveConsumer2(color -> {
            me.zacharias.speedometer.Config.setColor(color.getRed(), color.getGreen(), color.getBlue());
        })
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.knot"), me.zacharias.speedometer.Config.getUseKnot())
        .setSaveConsumer(me.zacharias.speedometer.Config::setUseKnot)
        .setYesNoTextSupplier(useKnot -> Component.translatable("speedometer.useKnot."+useKnot))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.visualSpeedometer"), me.zacharias.speedometer.Config.getVisualSpeedometer())
        .setSaveConsumer(me.zacharias.speedometer.Config::setVisualSpeedometer)
        .setYesNoTextSupplier((visualSpeedometer -> Component.translatable("speedometer.visualSpeedometer."+visualSpeedometer)))
        .setRequirement(Requirement.isFalse(Config::isDisableVisualSpeedometer))
        .build()
    );

    // Regex

    String xRegex = "W*w*S*s*\\+*-*\\**/*[0-9]*";
    String yRegex = "H*h*S*s*\\+*-*\\**/*[0-9]*";

    category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("speedometer.config.xPosition"), Config.getXPosition())
        .setSaveConsumer(Config::setXPosition)
        .setErrorSupplier(xPosition -> {
          if(xPosition.matches(xRegex)){
            return Optional.empty();
          }else{
            return Optional.of(Component.translatable("speedometer.invalid"));
          }
        })
        .setTooltip(
            Component.translatable("speedometer.config.tooltip.xPosition.line1"),
            Component.translatable("speedometer.config.tooltip.xPosition.line2"),
            Component.translatable("speedometer.config.tooltip.xPosition.line3")
        )
        .build()
    );


    category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("speedometer.config.yPosition"), Config.getYPosition())
        .setSaveConsumer(Config::setYPosition)
        .setErrorSupplier(yPosition -> {
          if(yPosition.matches(yRegex)){
            return Optional.empty();
          }else{
            return Optional.of(Component.translatable("speedometer.invalid"));
          }
        })
        .setTooltip(
            Component.translatable("speedometer.config.tooltip.yPosition.line1"),
            Component.translatable("speedometer.config.tooltip.yPosition.line2"),
            Component.translatable("speedometer.config.tooltip.yPosition.line3")
        )
        .build()
    );

    // Size of visual image

    category.addEntry(entryBuilder.startIntField(Component.translatable("speedometer.config.imageSize"), Config.getImageSize())
        .setSaveConsumer(Config::setImageSize)
        .setTooltip(Component.translatable("speedometer.config.tooltip.imageSize"))
        .build()
    );

    // Show visual speed type

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.showSpeedType"), Config.getShowSpeedType())
        .setSaveConsumer(Config::setShowSpeedType)
        .setYesNoTextSupplier(showSpeedType -> Component.translatable("speedometer."+(showSpeedType?"show":"hide")))
        .setTooltip(Component.translatable("speedometer.config.tooltip.showSpeedType.line1"))
        .build()
    );



    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.debug"),Config.isDebug())
        .setSaveConsumer(Config::setDebug)
        .setYesNoTextSupplier(isDebug -> Component.translatable("speedometer.debug."+isDebug))
        .setTooltip(Component.translatable("speedometer.config.tooltip.debug"))
        .build()
    );

    builder.setSavingRunnable(me.zacharias.speedometer.Config::save);

    return builder;
  }
}
