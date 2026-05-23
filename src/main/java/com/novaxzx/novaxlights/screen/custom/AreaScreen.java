package com.novaxzx.novaxlights.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.network.AreaLightPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class AreaScreen extends AbstractContainerScreen<AreaMenu> {

    private EditBox hexInput;
    private EditBox distanceInput;
    private EditBox brightnessInput;
    private EditBox AngleInput;
    private EditBox sizeXInput;
    private EditBox sizeYInput;

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovaxLights.MODID, "textures/gui/area/area_gui.png");

    public AreaScreen(AreaMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if(keyCode == 256) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if(hexInput.isFocused()) {
            if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return true;
            }

            if (hexInput.keyPressed(keyCode, scanCode, modifiers) || hexInput.canConsumeInput()) {
                return true;
            }

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Caixa da Distância

        distanceInput = new EditBox(
                this.font,
                x + 17,
                y + 15,
                23,
                18,
                Component.literal("distance")
        );

        distanceInput.setMaxLength(3);

        distanceInput.setValue(String.valueOf((int) menu.blockEntity.getDistance()));

        distanceInput.setFilter(text -> text.matches("\\d*"));

        distanceInput.setResponder(text -> {
            if(!text.isEmpty()) {
                PacketDistributor.sendToServer(
                        new AreaLightPacket(
                                menu.blockEntity.getBlockPos(),
                                0,
                                text
                        )
                );
            }
        });

        this.addRenderableWidget(distanceInput);

        // Caixa da Intensidade

        brightnessInput = new EditBox(
                this.font,
                x + 136,
                y + 15,
                23,
                18,
                Component.literal("brightness")
        );

        brightnessInput.setMaxLength(5);

        brightnessInput.setValue(String.valueOf(menu.blockEntity.getBrightness()));

        brightnessInput.setFilter(text ->
                text.matches("-?\\d*\\.?\\d*")
        );

        brightnessInput.setResponder(text -> {

            if(!text.isEmpty() && !text.equals(".")) {

                PacketDistributor.sendToServer(
                        new AreaLightPacket(
                                menu.blockEntity.getBlockPos(),
                                1,
                                text
                        )
                );
            }
        });

        this.addRenderableWidget(brightnessInput);

        // Caixa do Hexadecimal

        hexInput = new EditBox(
                this.font,
                x + 61,
                y + 15,
                54,
                18,
                Component.literal("hex")
        );

        hexInput.setMaxLength(6);

        hexInput.setValue(menu.blockEntity.getHexColor());

        hexInput.setResponder(text -> {
            PacketDistributor.sendToServer(
                    new AreaLightPacket(
                            menu.blockEntity.getBlockPos(),
                            2,
                            text
                    )
            );
        });


        this.addRenderableWidget(hexInput);

        // Caixa do Ângulo

        AngleInput = new EditBox(
                this.font,
                x + 76,
                y + 51,
                24,
                18,
                Component.literal("angle")
        );

        AngleInput.setMaxLength(5);

        AngleInput.setValue(String.valueOf(menu.blockEntity.getAngle()));

        AngleInput.setFilter(text ->
                text.matches("\\d*\\.?\\d*")
        );

        AngleInput.setResponder(text -> {

            if(!text.isEmpty() && !text.equals(".")) {

                PacketDistributor.sendToServer(
                        new AreaLightPacket(
                                menu.blockEntity.getBlockPos(),
                                3,
                                text
                        )
                );
            }
        });

        this.addRenderableWidget(AngleInput);

        // Caixa do Tamanho X

        sizeXInput = new EditBox(
                this.font,
                x + 17,
                y + 51,
                23,
                18,
                Component.literal("size_x")
        );

        sizeXInput.setMaxLength(5);

        sizeXInput.setValue(String.valueOf(menu.blockEntity.getSizeX()));

        sizeXInput.setFilter(text ->
                text.matches("\\d*\\.?\\d*")
        );

        sizeXInput.setResponder(text -> {

            if(!text.isEmpty() && !text.equals(".")) {

                PacketDistributor.sendToServer(
                        new AreaLightPacket(
                                menu.blockEntity.getBlockPos(),
                                4,
                                text
                        )
                );
            }
        });

        this.addRenderableWidget(sizeXInput);

        // Caixa do Tamanho Y

        sizeYInput = new EditBox(
                this.font,
                x + 136,
                y + 51,
                23,
                18,
                Component.literal("size_y")
        );

        sizeYInput.setMaxLength(5);

        sizeYInput.setValue(String.valueOf(menu.blockEntity.getSizeY()));

        sizeYInput.setFilter(text ->
                text.matches("-?\\d*\\.?\\d*")
        );

        sizeYInput.setResponder(text -> {

            if(!text.isEmpty() && !text.equals(".")) {

                PacketDistributor.sendToServer(
                        new AreaLightPacket(
                                menu.blockEntity.getBlockPos(),
                                5,
                                text
                        )
                );
            }
        });

        this.addRenderableWidget(sizeYInput);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
