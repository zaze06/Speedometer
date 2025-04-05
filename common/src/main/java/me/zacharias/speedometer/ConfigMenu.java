package me.zacharias.speedometer;

import me.shedaniel.clothconfig2.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Optional;

public class ConfigMenu {
  public static ConfigBuilder getConfig(Screen parent) {
    ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(new TranslatableComponent("speedometer.config.name"));

    ConfigCategory category = builder.getOrCreateCategory(new TranslatableComponent("speedometer.config.category.name"));

    ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    category.addEntry(entryBuilder.startEnumSelector(new TranslatableComponent("speedometer.config.speed"), SpeedTypes.class, me.zacharias.speedometer.Config.getSpeedType())
        .setEnumNameProvider(SpeedTypes::getName)
        .setSaveConsumer(me.zacharias.speedometer.Config::setSpeedType)
                    //.setDefaultValue(SpeedTypes.BlockPS)
        .build()
    );

    category.addEntry(entryBuilder.startColorField(new TranslatableComponent("speedometer.config.color"), me.zacharias.speedometer.Config.getColorRGB())
        .setSaveConsumer2(color -> Config.setColor(color.getRed(), color.getGreen(), color.getBlue()))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("speedometer.config.knot"), me.zacharias.speedometer.Config.getUseKnot())
        .setSaveConsumer(me.zacharias.speedometer.Config::setUseKnot)
        .setYesNoTextSupplier(useKnot -> new TranslatableComponent("speedometer.useKnot."+useKnot))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("speedometer.config.visualSpeedometer"), me.zacharias.speedometer.Config.getVisualSpeedometer())
        .setSaveConsumer(me.zacharias.speedometer.Config::setVisualSpeedometer)
        .setYesNoTextSupplier((visualSpeedometer -> new TranslatableComponent("speedometer.visualSpeedometer."+visualSpeedometer)))
        //.setRequirement(Requirement.isFalse(Config::isDisableVisualSpeedometer))
        .build()
    );

    // Regex

    String xRegex = "W*w*S*s*\\+*-*\\**/*[0-9]*";
    String yRegex = "H*h*S*s*\\+*-*\\**/*[0-9]*";

    category.addEntry(entryBuilder.startStringDropdownMenu(new TranslatableComponent("speedometer.config.xPosition"), Config.getXPosition())
        .setSaveConsumer(Config::setXPosition)
        .setErrorSupplier(xPosition -> {
          if(xPosition.matches(xRegex)){
            return Optional.empty();
          }else{
            return Optional.of(new TranslatableComponent("speedometer.invalid"));
          }
        })
        .setTooltip(
            new TranslatableComponent("speedometer.config.tooltip.xPosition.line1"),
            new TranslatableComponent("speedometer.config.tooltip.xPosition.line2"),
            new TranslatableComponent("speedometer.config.tooltip.xPosition.line3")
        )
        .build()
    );


    category.addEntry(entryBuilder.startStringDropdownMenu(new TranslatableComponent("speedometer.config.yPosition"), Config.getYPosition())
        .setSaveConsumer(Config::setYPosition)
        .setErrorSupplier(yPosition -> {
          if(yPosition.matches(yRegex)){
            return Optional.empty();
          }else{
            return Optional.of(new TranslatableComponent("speedometer.invalid"));
          }
        })
        .setTooltip(
            new TranslatableComponent("speedometer.config.tooltip.yPosition.line1"),
            new TranslatableComponent("speedometer.config.tooltip.yPosition.line2"),
            new TranslatableComponent("speedometer.config.tooltip.yPosition.line3")
        )
        .build()
    );

    // Size of visual image

    category.addEntry(entryBuilder.startIntField(new TranslatableComponent("speedometer.config.imageSize"), Config.getImageSize())
        .setSaveConsumer(Config::setImageSize)
        .setTooltip(new TranslatableComponent("speedometer.config.tooltip.imageSize"))
        .setErrorSupplier(size -> {
            if(size > 300 || size < 10)
            {
                return Optional.of(new TranslatableComponent("speedometer.config.error.size_outofbounds"));
            }
            else {
                return Optional.empty();
            }
        })
        .build()
    );

    // Show visual speed type

    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("speedometer.config.showSpeedType"), Config.getShowSpeedType())
        .setSaveConsumer(Config::setShowSpeedType)
        .setYesNoTextSupplier(showSpeedType -> new TranslatableComponent("speedometer."+(showSpeedType?"show":"hide")))
        .setTooltip(new TranslatableComponent("speedometer.config.tooltip.showSpeedType.line1"))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("speedometer.config.override_color"), Config.isOverrideColor())
        .setSaveConsumer(Config::setOverrideColor)
        .setTooltip(
                new TranslatableComponent("speedometer.config.tooltip.override_color.line1"),
                new TranslatableComponent("speedometer.config.tooltip.override_color.line2")
        )
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(new TranslatableComponent("speedometer.config.debug"),Config.isDebug())
        .setSaveConsumer(Config::setDebug)
        .setYesNoTextSupplier(isDebug -> new TranslatableComponent("speedometer.debug."+isDebug))
        .setTooltip(new TranslatableComponent("speedometer.config.tooltip.debug"))
        .build()
    );

    builder.setSavingRunnable(me.zacharias.speedometer.Config::save);

    return builder;
  }
}
