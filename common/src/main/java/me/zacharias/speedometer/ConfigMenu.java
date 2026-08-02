package me.zacharias.speedometer;

import java.util.Optional;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import static me.zacharias.speedometer.Config.MAX_IMAGE_SIZE;
import static me.zacharias.speedometer.Config.MIN_IMAGE_SIZE;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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

    category.addEntry(entryBuilder.startColorField(Component.translatable("speedometer.config.color"), me.zacharias.speedometer.Config.getColorRGB())
        .setSaveConsumer2(color -> Config.setColor(color.getRed(), color.getGreen(), color.getBlue()))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.knot"), me.zacharias.speedometer.Config.getUseKnot())
        .setSaveConsumer(me.zacharias.speedometer.Config::setUseKnot)
        .setYesNoTextSupplier(useKnot -> Component.translatable("speedometer.useKnot."+useKnot))
        .build()
    );

    category.addEntry(entryBuilder.startIntSlider(Component.translatable("speedometer.config.average_speed_sample_count"), me.zacharias.speedometer.Config.getSpeedAvrageSampleCount(), 30, 500)
        .setSaveConsumer(me.zacharias.speedometer.Config::setSpeedAvrageSampleCount)
        .build()
    );

    Integer[] speedPrecisionValues = new Integer[] { // 0 to 5
        Config.MIN_SPEED_PRECISION, 
        Config.MIN_SPEED_PRECISION + 1, 
        Config.MIN_SPEED_PRECISION + 2, 
        Config.MIN_SPEED_PRECISION + 3, 
        Config.MIN_SPEED_PRECISION + 4, 
        Config.MAX_SPEED_PRECISION
    };

    category.addEntry(entryBuilder.startSelector(
        Component.translatable("speedometer.config.speed_precision"),
        speedPrecisionValues,
        me.zacharias.speedometer.Config.getSpeedPrecision()
    )
        .setDefaultValue(2)
        .setNameProvider(value -> Component.literal(String.valueOf(value)))
        .setSaveConsumer(me.zacharias.speedometer.Config::setSpeedPrecision)
        .setTooltip(Component.translatable("speedometer.config.tooltip.speed_precision"))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.visualSpeedometer"), me.zacharias.speedometer.Config.getVisualSpeedometer())
        .setSaveConsumer(me.zacharias.speedometer.Config::setVisualSpeedometer)
        .setYesNoTextSupplier((visualSpeedometer -> Component.translatable("speedometer.visualSpeedometer."+visualSpeedometer)))
        .setRequirement(Requirement.isFalse(Config::isDisableVisualSpeedometer))
        .build()
    );

    category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("speedometer.config.xPosition"), Config.getXPosition())
        .setSaveConsumer(Config::setXPosition)
        .setErrorSupplier(xPosition -> {
            if(xPosition.isEmpty())
            {
                return Optional.of(Component.translatable("speedometer.invalid"));
            }
            try {
                Expression ex = new ExpressionBuilder(xPosition)
                        .variables("W","w","s")
                        .build()
                        .setVariable("W", 0)
                        .setVariable("w", 0)
                        .setVariable("s", 0);
                if(ex.validate().isValid()){
                    return Optional.empty();
                }else{
                    return Optional.of(Component.translatable("speedometer.invalid"));
                }
            } catch (Exception e) {
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
            if(yPosition.isEmpty())
            {
                return Optional.of(Component.translatable("speedometer.invalid"));
            }
            try {
                Expression ex = new ExpressionBuilder(yPosition)
                        .variables("H", "h", "s")
                        .build()
                        .setVariable("H", 0)
                        .setVariable("h", 0)
                        .setVariable("s", 0);
                if (ex.validate().isValid()) {
                    return Optional.empty();
                } else {
                    return Optional.of(Component.translatable("speedometer.invalid"));
                }
            }
            catch (Exception ignored)
            {
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

    category.addEntry(entryBuilder.startIntSlider(Component.translatable("speedometer.config.imageSize"), Config.getImageSize(), MIN_IMAGE_SIZE, MAX_IMAGE_SIZE)
        .setSaveConsumer(Config::setImageSize)
        .setTooltip(Component.translatable("speedometer.config.tooltip.imageSize"))
        .setErrorSupplier(size -> {
            if(size > MAX_IMAGE_SIZE || size < MIN_IMAGE_SIZE)
            {
                return Optional.of(Component.translatable("speedometer.config.error.size_outofbounds"));
            }
            else {
                return Optional.empty();
            }
        })
        .build()
    );

    // Show visual speed type

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.showSpeedType"), Config.getShowSpeedType())
        .setSaveConsumer(Config::setShowSpeedType)
        .setYesNoTextSupplier(showSpeedType -> Component.translatable("speedometer."+(showSpeedType?"show":"hide")))
        .setTooltip(Component.translatable("speedometer.config.tooltip.showSpeedType.line1"))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.override_color"), Config.isOverrideColor())
        .setSaveConsumer(Config::setOverrideColor)
        .setTooltip(
                Component.translatable("speedometer.config.tooltip.override_color.line1"),
                Component.translatable("speedometer.config.tooltip.override_color.line2")
        )
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
