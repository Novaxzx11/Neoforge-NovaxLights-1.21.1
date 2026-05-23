package com.novaxzx.novaxlights.entity;

import com.novaxzx.novaxlights.Config;
import com.novaxzx.novaxlights.block.ModBlocks;
import com.novaxzx.novaxlights.client.ClientConfig;
import com.novaxzx.novaxlights.screen.custom.AreaMenu;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AreaLightBlockEntity extends BlockEntity implements MenuProvider {

    private AreaLightData light;
    private LightRenderHandle<AreaLightData> handle;

    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float distance = 16.0f;
    private float brightness = 1.0f;
    private String hexColor = "FFFFFF";
    private float angle = 0.3f;
    private float sizeX = 1.0f;
    private float sizeY = 1.0f;

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

        tag.putFloat("brightness", brightness);

        tag.putString("hexColor", hexColor);

        tag.putFloat("angle", angle);

        tag.putFloat("sizeX", sizeX);
        tag.putFloat("sizeY", sizeY);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        red = tag.getFloat("red");
        green = tag.getFloat("green");
        blue = tag.getFloat("blue");

        distance = tag.getFloat("distance");

        brightness = tag.getFloat("brightness");

        hexColor = tag.getString("hexColor");
        updateColorFromHex();

        angle = tag.getFloat("angle");

        sizeX = tag.getFloat("sizeX");
        sizeY = tag.getFloat("sizeY");
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

            light.setSize(sizeX, sizeY);

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

    public void setHexColor(String hex) {

        // Remove #
        hex = hex.replace("#", "");

        // Validação
        if(!hex.matches("[0-9a-fA-F]{6}")) {
            hex = "FFFFFF";
        }

        this.hexColor = hex.toUpperCase();

        int red1 = Character.digit(hex.charAt(0), 16);
        int red2 = Character.digit(hex.charAt(1), 16);
        int green1 = Character.digit(hex.charAt(2), 16);
        int green2 = Character.digit(hex.charAt(3), 16);
        int blue1 = Character.digit(hex.charAt(4), 16);
        int blue2 = Character.digit(hex.charAt(5), 16);

        int redInt = (red1 * 16) + red2;
        int greenInt = (green1 * 16) + green2;
        int blueInt = (blue1 * 16) + blue2;

        this.red = redInt / 255f;
        this.green = greenInt / 255f;
        this.blue = blueInt / 255f;

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

    private void updateColorFromHex() {

        String hex = hexColor.replace("#", "");

        if(!hex.matches("[0-9a-fA-F]{6}")) {
            hex = "FFFFFF";
        }

        int redInt = Integer.parseInt(hex.substring(0, 2), 16);
        int greenInt = Integer.parseInt(hex.substring(2, 4), 16);
        int blueInt = Integer.parseInt(hex.substring(4, 6), 16);

        this.red = redInt / 255f;
        this.green = greenInt / 255f;
        this.blue = blueInt / 255f;

        if(light != null) {
            light.setColor(red, green, blue);
        }
    }

    public void setDistanceFromText(String text) {
        try {
            int value = Integer.parseInt(text);
            distance = Math.max(
                    0,
                    Math.min(ClientConfig.MAX_DISTANCE, value)
            );

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

        } catch (Exception ignored) {}
    }

    public void setBrightnessFromText(String text) {

        try {
            float value = Float.parseFloat(text);

            brightness = Math.max(
                    -1.0f,
                    Math.min((float) ClientConfig.MAX_BRIGHTNESS, value)
            );

            brightness = Math.round(brightness * 10f) / 10f;

            if(light != null) {
                light.setBrightness(brightness);
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

        } catch (Exception ignored) {}
    }

    public void setAngleFromText(String text) {

        try {
            float value = Float.parseFloat(text);

            angle = Math.max(
                    0.1f,
                    Math.min((float) ClientConfig.MAX_ANGLE, value)
            );

            angle = Math.round(angle * 10f) / 10f;

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

        } catch (Exception ignored) {}
    }

    public void setSizeXFromText(String text) {

        try {
            float value = Float.parseFloat(text);

            sizeX = Math.max(
                    0.1f,
                    Math.min((float) ClientConfig.MAX_X_SIZE, value)
            );

            sizeX = Math.round(sizeX * 10f) / 10f;

            if(light != null) {
                light.setSize(sizeX, sizeY);
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

        } catch (Exception ignored) {}
    }

    public void setSizeYFromText(String text) {

        try {
            float value = Float.parseFloat(text);

            sizeY = Math.max(
                    0.1f,
                    Math.min((float) ClientConfig.MAX_Y_SIZE, value)
            );

            sizeY = Math.round(sizeY * 10f) / 10f;

            if(light != null) {
                light.setSize(sizeX, sizeY);
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

        } catch (Exception ignored) {}
    }

    public void clientTick() {

        if(level == null || !level.isClientSide || light == null) {
            return;
        }

        light.setColor(red, green, blue);

        light.setDistance(distance);

        light.setBrightness(brightness);

        light.setAngle(angle);

        light.setSize(sizeX, sizeY);

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
    }

    public float getDistance() {
        return distance;
    }

    public float getBrightness() {
        return brightness;
    }

    public float getAngle() {
        return angle;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public String getHexColor() {
        return hexColor;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if(handle != null) {
            handle.close();
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Area Menu");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new AreaMenu(i, inventory, this);
    }
}