package com.novaxzx.novaxlights.block.custom;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.block.ModBlocks;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public static class LightBlockEntity extends BlockEntity {
    public LightBlockEntity(BlockPos pos, BlockState state) {
        super(LIGHT_BLOCK_ENTITY.get(), pos, state);
    }

}

public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NovaxLights.MODID);

public static final Supplier<BlockEntityType<LightBlockEntity>> LIGHT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "light_block_entity",
        // The block entity type.
        () -> new BlockEntityType<>(
                // The supplier to use for constructing the block entity instances.
                LightBlockEntity::new,
                // An optional value that, when true, only allows players with OP permissions
                // to load NBT data (e.g. placing a block item)
                false,
                // A vararg of blocks that can have this block entity.
                // This assumes the existence of the referenced blocks as DeferredBlock<Block>s.
                ModBlocks.LIGHT_BLOCK.get();
        )
);