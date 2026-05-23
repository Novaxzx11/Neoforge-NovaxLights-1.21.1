package com.novaxzx.novaxlights;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER =
            new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_DISTANCE = BUILDER
                    .comment("config.novaxlights.max_distance.tooltip")
                    .translation("config.novaxlights.max_distance")
                    .defineInRange("maxDistance", 32, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_BRIGHTNESS = BUILDER
                    .comment("config.novaxlights.max_brightness.tooltip")
                    .translation("config.novaxlights.max_brightness")
                    .defineInRange("maxBrightness", 4.0, -1.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_X_SIZE = BUILDER
                    .comment("config.novaxlights.max_x_size.tooltip")
                    .translation("config.novaxlights.max_x_size")
                    .defineInRange("maxXSize", 10.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_Y_SIZE = BUILDER
                    .comment("config.novaxlights.max_y_size.tooltip")
                    .translation("config.novaxlights.max_y_size")
                    .defineInRange("maxYSize", 10.0, 0.0, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_ANGLE = BUILDER
                    .comment("config.novaxlights.max_angle.tooltip")
                    .translation("config.novaxlights.max_angle")
                    .defineInRange("maxAngle", 4.0, 0.1, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue FLASHLIGHT_CHANGE_COLOR = BUILDER
                    .comment("config.novaxlights.flashlight_change_color.tooltip")
                    .translation("config.novaxlights.flashlight_change_color")
                    .define("flashlightChangeColor", true);

    public static final ModConfigSpec.DoubleValue FLASHLIGHT_BRIGHTNESS = BUILDER
                    .comment("config.novaxlights.flashlight_brightness.tooltip")
                    .translation("config.novaxlights.flashlight_brightness")
                    .defineInRange("flashlightBrightness", 2.0, -1, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue FLASHLIGHT_DISTANCE = BUILDER
                    .comment("config.novaxlights.flashlight_distance.tooltip")
                    .translation("config.novaxlights.flashlight_distance")
                    .defineInRange("flashlightDistance", 30.0, 0.1, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue FLASHLIGHT_ANGLE = BUILDER
                    .comment("config.novaxlights.flashlight_angle.tooltip")
                    .translation("config.novaxlights.flashlight_angle")
                    .defineInRange("flashlightAngle", 0.3, 0.1, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue FLASHLIGHT_SIZE = BUILDER
                    .comment("config.novaxlights.flashlight_size.tooltip")
                    .translation("config.novaxlights.flashlight_size")
                    .defineInRange("flashlightSize", 1.3, 0.1, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLASHLIGHT_DRAIN_RATE = BUILDER
                    .comment("config.novaxlights.flashlight_drain_rate.tooltip")
                    .translation("config.novaxlights.flashlight_drain_rate")
                    .defineInRange("flashlightDrainRate", 200, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLASHLIGHT_DAMAGE = BUILDER
                    .comment("config.novaxlights.flashlight_damage.tooltip")
                    .translation("config.novaxlights.flashlight_damage")
                    .defineInRange("flashlightDamage", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec SERVER_SPEC = BUILDER.build();
}