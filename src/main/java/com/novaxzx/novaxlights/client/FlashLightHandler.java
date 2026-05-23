package com.novaxzx.novaxlights.client;

import com.novaxzx.novaxlights.Config;
import com.novaxzx.novaxlights.item.ModItems;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.HashMap;
import java.util.Map;

public class FlashLightHandler {
    private static final Map<Integer, AreaLightData> LIGHTS =
            new HashMap<>();

    private static final Map<Integer, LightRenderHandle<AreaLightData>> HANDLES =
            new HashMap<>();

    public static void clientTick() {

        Minecraft mc = Minecraft.getInstance();

        if(mc.level == null) {
            return;
        }

        for(Player player : mc.level.players()) {

            ItemStack stack = player.getMainHandItem();
            ItemStack stack2 = player.getOffhandItem();

            boolean holding =
                    stack.is(ModItems.FLASHLIGHT.get())
                            && ModItems.FlashLightItem.isOn(stack);

            boolean holding2 =
                    stack2.is(ModItems.FLASHLIGHT.get())
                            && ModItems.FlashLightItem.isOn(stack2);

            int id = player.getId();

            if(!holding && !holding2) {

                removeLight(id);

                continue;
            }

            AreaLightData light = LIGHTS.get(id);

            if(light == null) {

                light = createLight();

                LightRenderHandle<AreaLightData> handle =
                        VeilRenderSystem.renderer()
                                .getLightRenderer()
                                .addLight(light);

                LIGHTS.put(id, light);
                HANDLES.put(id, handle);
            }

            updateLight(player, light, holding ? stack : stack2);
        }
    }

    private static AreaLightData createLight() {

        AreaLightData light = new AreaLightData();

        light.setBrightness((float) ClientConfig.FLASHLIGHT_BRIGHTNESS);

        light.setDistance((float) ClientConfig.FLASHLIGHT_DISTANCE);

        light.setAngle((float) ClientConfig.FLASHLIGHT_ANGLE);

        light.setSize((float) ClientConfig.FLASHLIGHT_SIZE, (float) ClientConfig.FLASHLIGHT_SIZE);

        light.setColor(1f, 1f, 0.9f);

        return light;
    }

    private static void removeLight(int id) {

        LightRenderHandle<AreaLightData> handle =
                HANDLES.remove(id);

        if(handle != null) {
            handle.close();
        }

        LIGHTS.remove(id);
    }

    private static void updateLight(Player player, AreaLightData light, ItemStack stack) {

        DyedItemColor dyed =
                stack.get(DataComponents.DYED_COLOR);

        int color = dyed != null ? dyed.rgb() : 0xFFF5CC;

        float r = ((color >> 16) & 255) / 255f;
        float g = ((color >> 8) & 255) / 255f;
        float b = (color & 255) / 255f;

        light.setColor(r, g, b);

        var look = player.getLookAngle().normalize();

        float forward = 1.0f;

        // posição da luz
        light.getPosition().set(
                (float)(player.getX() + look.x * forward),
                (float)(player.getEyeY() + look.y * forward),
                (float)(player.getZ() + look.z * forward)
        );

        // orientação correta
        light.getOrientation().identity();

        light.getOrientation().lookAlong(
                (float)-look.x,
                (float)-look.y,
                (float)-look.z,
                0f,
                1f,
                0f
        );
    }

}
