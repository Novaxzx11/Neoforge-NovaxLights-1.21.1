package com.novaxzx.novaxlights.item;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NovaxLights.MODID);

    public static final Supplier<CreativeModeTab> NOVAX_LIGHTS_TAB = CREATIVE_MODE_TAB.register("novax_lights_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.POINT_LIGHT_BLOCK))
                    .title(Component.translatable("creativetab.novaxlights.novax_lights"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.POINT_LIGHT_BLOCK);
                        output.accept(ModBlocks.AREA_LIGHT_BLOCK);
                        output.accept(ModItems.LIGHTEDITOR);
                        output.accept(ModItems.FLASHLIGHT);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
