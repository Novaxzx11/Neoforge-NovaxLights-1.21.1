package com.novaxzx.novaxlights.entity;

import com.novaxzx.novaxlights.Config;
import com.novaxzx.novaxlights.client.ClientConfig;
import com.novaxzx.novaxlights.screen.custom.PointMenu;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.core.BlockPos;
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
import foundry.veil.api.client.render.VeilRenderSystem;
import org.jetbrains.annotations.Nullable;

public class PointLightBlockEntity extends BlockEntity implements MenuProvider {

    private PointLightData light;
    private LightRenderHandle<PointLightData> handle;

    private float red = 1.0f;
    private float green = 1.0f;
    private float blue = 1.0f;
    private float distance = 15.0f;
    private float brightness = 1.0f;
    private String hexColor = "FFFFFF";

    public PointLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POINT_LIGHT_BE.get(), pos, state);
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

            light.setOcclusionEnabled(true);

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

    public void clientTick() {

        if(level == null || !level.isClientSide || light == null) {
            return;
        }

        light.setColor(red, green, blue);

        light.setRadius(distance);

        light.setBrightness(brightness);
    }

    public float getDistance() {
        return distance;
    }

    public float getBrightness() {
        return brightness;
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
        return Component.literal("Point Menu");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PointMenu(i, inventory, this);
    }
}