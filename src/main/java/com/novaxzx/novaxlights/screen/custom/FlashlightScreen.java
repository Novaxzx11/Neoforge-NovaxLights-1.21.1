package com.novaxzx.novaxlights.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.network.FlashlightColorPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.network.PacketDistributor;

public class FlashlightScreen extends AbstractContainerScreen<FlashlightMenu> {

    private EditBox colorBox;

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(NovaxLights.MODID, "textures/gui/flashlight/flashlight_gui.png");

    public FlashlightScreen(FlashlightMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {

        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        colorBox = new EditBox(
                this.font,
                x + 61,
                y + 33,
                54,
                18,
                Component.literal("Color")
        );

        colorBox.setMaxLength(6);

        ItemStack stack =
                minecraft.player.getMainHandItem();

        DyedItemColor dyed =
                stack.get(DataComponents.DYED_COLOR);

        int color =
                dyed != null
                        ? dyed.rgb()
                        : 0xFFF5CC;

        colorBox.setValue(
                String.format("%06X", color)
        );

        addRenderableWidget(colorBox);

        addRenderableWidget(
                Button.builder(
                        Component.literal("Save"),
                        button -> saveColor()
                ).bounds(
                        x + 61,
                        y + 56,
                        54,
                        18
                ).build()
        );
    }

    private void saveColor() {

        String text = colorBox.getValue();

        try {

            if(text.startsWith("#")) {
                text = text.substring(1);
            }

            int color =
                    Integer.parseInt(text, 16);

            PacketDistributor.sendToServer(
                    new FlashlightColorPacket(color)
            );

            minecraft.player.closeContainer();

        } catch (Exception ignored) {

        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

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