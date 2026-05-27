package org.eneryleen.damage_indicator.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreenFactory {

    public static Screen create(Screen parent) {
        DamageIndicatorConfig config = DamageIndicatorConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.damage_indicator.title"))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Display category
        ConfigCategory display = builder.getOrCreateCategory(Component.translatable("config.damage_indicator.category.display"));

        display.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.damage_indicator.enabled"), config.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.damage_indicator.enabled.tooltip"))
                .setSaveConsumer(newValue -> config.enabled = newValue)
                .build());

        display.addEntry(entryBuilder.startDoubleField(Component.translatable("config.damage_indicator.maxRenderDistance"), config.maxRenderDistance)
                .setDefaultValue(32.0)
                .setMin(8.0)
                .setMax(128.0)
                .setTooltip(Component.translatable("config.damage_indicator.maxRenderDistance.tooltip"))
                .setSaveConsumer(newValue -> config.maxRenderDistance = newValue)
                .build());

        display.addEntry(entryBuilder.startFloatField(Component.translatable("config.damage_indicator.baseScale"), config.baseScale)
                .setDefaultValue(0.025f)
                .setMin(0.01f)
                .setMax(0.1f)
                .setTooltip(Component.translatable("config.damage_indicator.baseScale.tooltip"))
                .setSaveConsumer(newValue -> config.baseScale = newValue)
                .build());

        // Text category
        ConfigCategory text = builder.getOrCreateCategory(Component.translatable("config.damage_indicator.category.text"));

        text.addEntry(entryBuilder.startStrField(Component.translatable("config.damage_indicator.damageFormat"), config.damageFormat)
                .setDefaultValue("%.1f")
                .setTooltip(Component.translatable("config.damage_indicator.damageFormat.tooltip"))
                .setSaveConsumer(newValue -> config.damageFormat = newValue)
                .build());

        text.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.damage_indicator.showDecimals"), config.showDecimals)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.damage_indicator.showDecimals.tooltip"))
                .setSaveConsumer(newValue -> config.showDecimals = newValue)
                .build());

        // Color category
        ConfigCategory color = builder.getOrCreateCategory(Component.translatable("config.damage_indicator.category.color"));

        color.addEntry(entryBuilder.startColorField(Component.translatable("config.damage_indicator.normalColor"), config.normalColor)
                .setDefaultValue(0xFFFFFF)
                .setTooltip(Component.translatable("config.damage_indicator.normalColor.tooltip"))
                .setSaveConsumer(newValue -> config.normalColor = newValue)
                .build());

        color.addEntry(entryBuilder.startColorField(Component.translatable("config.damage_indicator.criticalColor"), config.criticalColor)
                .setDefaultValue(0xFFFF55)
                .setTooltip(Component.translatable("config.damage_indicator.criticalColor.tooltip"))
                .setSaveConsumer(newValue -> config.criticalColor = newValue)
                .build());

        // Animation category
        ConfigCategory animation = builder.getOrCreateCategory(Component.translatable("config.damage_indicator.category.animation"));

        animation.addEntry(entryBuilder.startLongField(Component.translatable("config.damage_indicator.lifetime"), config.lifetime)
                .setDefaultValue(1200L)
                .setMin(500L)
                .setMax(5000L)
                .setTooltip(Component.translatable("config.damage_indicator.lifetime.tooltip"))
                .setSaveConsumer(newValue -> config.lifetime = newValue)
                .build());

        animation.addEntry(entryBuilder.startLongField(Component.translatable("config.damage_indicator.popDuration"), config.popDuration)
                .setDefaultValue(200L)
                .setMin(50L)
                .setMax(1000L)
                .setTooltip(Component.translatable("config.damage_indicator.popDuration.tooltip"))
                .setSaveConsumer(newValue -> config.popDuration = newValue)
                .build());

        animation.addEntry(entryBuilder.startLongField(Component.translatable("config.damage_indicator.fadeStartTime"), config.fadeStartTime)
                .setDefaultValue(1000L)
                .setMin(500L)
                .setMax(4000L)
                .setTooltip(Component.translatable("config.damage_indicator.fadeStartTime.tooltip"))
                .setSaveConsumer(newValue -> config.fadeStartTime = newValue)
                .build());

        animation.addEntry(entryBuilder.startDoubleField(Component.translatable("config.damage_indicator.verticalSpeed"), config.verticalSpeed)
                .setDefaultValue(1.5)
                .setMin(0.5)
                .setMax(5.0)
                .setTooltip(Component.translatable("config.damage_indicator.verticalSpeed.tooltip"))
                .setSaveConsumer(newValue -> config.verticalSpeed = newValue)
                .build());

        animation.addEntry(entryBuilder.startDoubleField(Component.translatable("config.damage_indicator.horizontalSpread"), config.horizontalSpread)
                .setDefaultValue(0.5)
                .setMin(0.0)
                .setMax(2.0)
                .setTooltip(Component.translatable("config.damage_indicator.horizontalSpread.tooltip"))
                .setSaveConsumer(newValue -> config.horizontalSpread = newValue)
                .build());

        // Critical category
        ConfigCategory critical = builder.getOrCreateCategory(Component.translatable("config.damage_indicator.category.critical"));

        critical.addEntry(entryBuilder.startFloatField(Component.translatable("config.damage_indicator.criticalScaleMultiplier"), config.criticalScaleMultiplier)
                .setDefaultValue(1.25f)
                .setMin(1.0f)
                .setMax(2.0f)
                .setTooltip(Component.translatable("config.damage_indicator.criticalScaleMultiplier.tooltip"))
                .setSaveConsumer(newValue -> config.criticalScaleMultiplier = newValue)
                .build());

        critical.addEntry(entryBuilder.startFloatField(Component.translatable("config.damage_indicator.criticalPopEffect"), config.criticalPopEffect)
                .setDefaultValue(0.5f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Component.translatable("config.damage_indicator.criticalPopEffect.tooltip"))
                .setSaveConsumer(newValue -> config.criticalPopEffect = newValue)
                .build());

        critical.addEntry(entryBuilder.startFloatField(Component.translatable("config.damage_indicator.normalPopEffect"), config.normalPopEffect)
                .setDefaultValue(0.2f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setTooltip(Component.translatable("config.damage_indicator.normalPopEffect.tooltip"))
                .setSaveConsumer(newValue -> config.normalPopEffect = newValue)
                .build());

        return builder.build();
    }
}
