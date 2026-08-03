package me.zacharias.speedometer;

import java.util.Optional;
import java.util.stream.IntStream;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import static me.zacharias.speedometer.Config.*;

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
        .setDefaultValue(DEFAULT_SPEED_TYPE)
        .build()
    );

    category.addEntry(entryBuilder.startColorField(Component.translatable("speedometer.config.color"), me.zacharias.speedometer.Config.getColorRGB())
        .setSaveConsumer2(color -> Config.setColor(color.getRed(), color.getGreen(), color.getBlue()))
        // The color format is 0xRRGGBBAA. Since we don't use the alpha (AA) channel,
        // we AND the value with 0xFFFFFF to keep only the RGB components.
        .setDefaultValue(DEFAULT_COLOR.getRGB() & 0xFFFFFF)
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.knot"), me.zacharias.speedometer.Config.getUseKnot())
        .setSaveConsumer(me.zacharias.speedometer.Config::setUseKnot)
        .setYesNoTextSupplier(useKnot -> Component.translatable("speedometer.useKnot."+useKnot))
        .setDefaultValue(DEFAULT_USE_KNOT)
        .build()
    );

    category.addEntry(entryBuilder.startIntSlider(Component.translatable("speedometer.config.average_speed_sample_count"), me.zacharias.speedometer.Config.getSpeedAvrageSampleCount(), 30, 500)
        .setSaveConsumer(me.zacharias.speedometer.Config::setSpeedAvrageSampleCount)
        .setDefaultValue(DEFAULT_SPEED_AVERAGE_SAMPLE_COUNT)
        .build()
    );

    Integer[] speedPrecisionValues = IntStream.rangeClosed(Config.MIN_SPEED_PRECISION, Config.MAX_SPEED_PRECISION).boxed().toArray(Integer[]::new);

    category.addEntry(entryBuilder.startSelector(
        Component.translatable("speedometer.config.speed_precision"),
        speedPrecisionValues,
        me.zacharias.speedometer.Config.getSpeedPrecision()
    )
        .setDefaultValue(DEFAULT_SPEED_PRECISION)
        .setNameProvider(value -> Component.literal(String.valueOf(value)))
        .setSaveConsumer(me.zacharias.speedometer.Config::setSpeedPrecision)
        .setTooltip(Component.translatable("speedometer.config.tooltip.speed_precision"))
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.visualSpeedometer"), me.zacharias.speedometer.Config.getVisualSpeedometer())
        .setSaveConsumer(me.zacharias.speedometer.Config::setVisualSpeedometer)
        .setYesNoTextSupplier((visualSpeedometer -> Component.translatable("speedometer.visualSpeedometer."+visualSpeedometer)))
        .setRequirement(Requirement.isFalse(Config::isDisableVisualSpeedometer))
        .setDefaultValue(DEFAULT_VISUAL_SPEEDOMETER)
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
        .setDefaultValue(DEFAULT_X_POSITION)
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
        .setDefaultValue(DEFAULT_Y_POSITION)
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
        .setDefaultValue(DEFAULT_IMAGE_SIZE)
        .build()
    );

    // Show visual speed type

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.showSpeedType"), Config.getShowSpeedType())
        .setSaveConsumer(Config::setShowSpeedType)
        .setYesNoTextSupplier(showSpeedType -> Component.translatable("speedometer."+(showSpeedType?"show":"hide")))
        .setTooltip(Component.translatable("speedometer.config.tooltip.showSpeedType.line1"))
        .setDefaultValue(DEFAULT_SHOW_SPEED_TYPE)
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.override_color"), Config.isOverrideColor())
        .setSaveConsumer(Config::setOverrideColor)
        .setTooltip(
                Component.translatable("speedometer.config.tooltip.override_color.line1"),
                Component.translatable("speedometer.config.tooltip.override_color.line2")
        )
        .setDefaultValue(DEFAULT_OVERRIDE_COLOR)
        .build()
    );

    category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("speedometer.config.debug"),Config.isDebug())
        .setSaveConsumer(Config::setDebug)
        .setYesNoTextSupplier(isDebug -> Component.translatable("speedometer.debug."+isDebug))
        .setTooltip(Component.translatable("speedometer.config.tooltip.debug"))
        .setDefaultValue(DEFAULT_DEBUG)
        .build()
    );

    builder.setSavingRunnable(me.zacharias.speedometer.Config::save);

    return builder;
  }
}
