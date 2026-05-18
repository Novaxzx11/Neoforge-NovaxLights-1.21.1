package com.novaxzx.novaxlights.entity;

import com.novaxzx.novaxlights.block.ModBlocks;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import foundry.veil.api.client.render.VeilRenderSystem;

public class LightBlockEntity extends BlockEntity {

    private PointLightData light;
    private LightRenderHandle<PointLightData> handle;

    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float distance = 15.0f;

    public LightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIGHT_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putFloat("Red", red);
        tag.putFloat("Green", green);
        tag.putFloat("Blue", blue);

        tag.putFloat("Distance", distance);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        red = tag.getFloat("Red");
        green = tag.getFloat("Green");
        blue = tag.getFloat("Blue");

        distance = tag.getFloat("Distance");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if(level != null && level.isClientSide) {

            this.light = new PointLightData();

            light.setBrightness(1.0f).setColor(red, green, blue);

            light.setRadius(distance);

            light.setPosition(
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5
            );

            this.handle = VeilRenderSystem.renderer()
                    .getLightRenderer()
                    .addLight(light);
        }
    }

    public void setLightColor(DyeColor dyeColor) {

        int rgb = dyeColor.getTextureDiffuseColor();

        this.red = ((rgb >> 16) & 255) / 255f;
        this.green = ((rgb >> 8) & 255) / 255f;
        this.blue = (rgb & 255) / 255f;

        if(light != null) {
            light.setColor(red, green, blue);
        }

        setChanged();

        if(level != null) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public void changeDistance(boolean decrease) {

        if(decrease) {
            distance -= 1.0f;
        } else {
            distance += 1.0f;
        }

        distance = Math.max(0.0f, Math.min(16.0f, distance));

        if(light != null) {
            light.setRadius(distance);
        }

        setChanged();

        if(level != null) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public void clientTick() {

        if(level == null || !level.isClientSide || light == null) {
            return;
        }

        light.setColor(red, green, blue);

        light.setRadius(distance);

        int power = getBlockState().getValue(ModBlocks.LightBlock.POWER);

        float brightness = power / 15.0f;

        light.setBrightness(brightness * 10.0f);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if(handle != null) {
            handle.close();
        }
    }
}