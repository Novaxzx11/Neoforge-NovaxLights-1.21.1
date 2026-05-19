package com.novaxzx.novaxlights.entity;

import com.novaxzx.novaxlights.Config;
import com.novaxzx.novaxlights.block.ModBlocks;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AreaLightBlockEntity extends BlockEntity {

    private AreaLightData light;
    private LightRenderHandle<AreaLightData> handle;

    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float distance = 16.0f;
    private float angle = 0.3f;

    public AreaLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AREA_LIGHT_BE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putFloat("red", red);
        tag.putFloat("green", green);
        tag.putFloat("blue", blue);

        tag.putFloat("distance", distance);

        tag.putFloat("angle", angle);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        red = tag.getFloat("red");
        green = tag.getFloat("green");
        blue = tag.getFloat("blue");

        distance = tag.getFloat("distance");

        angle = tag.getFloat("angle");
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

            this.light = new AreaLightData();

            light.setBrightness(1.0f).setColor(red, green, blue);

            light.setDistance(distance);

            light.setOcclusionEnabled(true);

            Direction facing =
                    getBlockState().getValue(ModBlocks.AreaLightBlock.FACING);

            float offsetX = 0.0f;
            float offsetY = 0.0f;
            float offsetZ = 0.0f;

            switch(facing) {
                case UP -> offsetY = 0.6f;
                case DOWN -> offsetY = -0.6f;
                case NORTH -> offsetZ = -0.6f;
                case SOUTH -> offsetZ = 0.6f;
                case EAST -> offsetX = 0.6f;
                case WEST -> offsetX = -0.6f;
            }

            light.getPosition().set(
                    worldPosition.getX() + 0.5f + (offsetX),
                    worldPosition.getY() + 0.5f + (offsetY),
                    worldPosition.getZ() + 0.5 + (offsetZ)
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

        distance = Math.max(0.0f, Math.min(Config.MAX_DISTANCE.get(), distance));

        if(light != null) {
            light.setDistance(distance);
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

    public void changeAngle(boolean decrease) {

        if(decrease) {
            angle -= Config.ANGLE_EDITOR_VALUE.get().floatValue();
        } else {
            angle += Config.ANGLE_EDITOR_VALUE.get().floatValue();
        }

        angle = Math.max(0.0f, Math.min(Config.MAX_ANGLE.get().floatValue(), angle));

        if(light != null) {
            light.setAngle(angle);
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

        light.setDistance(distance);

        light.setAngle(angle);

        Direction facing =
                getBlockState().getValue(ModBlocks.AreaLightBlock.FACING);

        switch (facing) {
            case NORTH -> light.getOrientation().set(0, 1, 0, 0);
            case SOUTH -> light.getOrientation().set(0, 0, 0, 1);
            case EAST -> light.getOrientation().set(0, -0.707f, 0, 0.707f);
            case WEST -> light.getOrientation().set(0, 0.707f, 0, 0.707f);
            case UP -> light.getOrientation().set(0.707f, 0, 0, 0.707f);
            case DOWN -> light.getOrientation().set(-0.707f, 0, 0, 0.707f);
        }

        int power = getBlockState().getValue(ModBlocks.AreaLightBlock.POWER);

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