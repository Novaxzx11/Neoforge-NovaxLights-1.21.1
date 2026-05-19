package com.novaxzx.novaxlights.entity;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.block.ModBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NovaxLights.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PointLightBlockEntity>> POINT_LIGHT_BE =
            BLOCK_ENTITIES.register("point_light_be", () ->
                    BlockEntityType.Builder.of(
                            PointLightBlockEntity::new,
                            ModBlocks.POINT_LIGHT_BLOCK.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AreaLightBlockEntity>> AREA_LIGHT_BE =
            BLOCK_ENTITIES.register("area_light_be", () ->
                    BlockEntityType.Builder.of(
                            AreaLightBlockEntity::new,
                            ModBlocks.AREA_LIGHT_BLOCK.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}