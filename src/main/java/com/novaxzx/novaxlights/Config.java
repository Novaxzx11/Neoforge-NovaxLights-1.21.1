package com.novaxzx.novaxlights;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_DISTANCE = BUILDER
            .comment("config.novaxlights.max_distance.tooltip")
            .translation("config.novaxlights.max_distance")
            .defineInRange("maxDistance", 16, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MAX_ANGLE = BUILDER
            .comment("config.novaxlights.max_angle.tooltip")
            .translation("config.novaxlights.max_angle")
            .defineInRange("maxAngle", 4, 0.1, Double.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue ANGLE_EDITOR_VALUE = BUILDER
            .comment("config.novaxlights.angle_editor_value.tooltip")
            .translation("config.novaxlights.angle_editor_value")
            .defineInRange("angleEditorValue", 0.1, 0.001, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
