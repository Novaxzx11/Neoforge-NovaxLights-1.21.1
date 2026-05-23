package com.novaxzx.novaxlights.network;

import com.novaxzx.novaxlights.NovaxLights;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FlashlightColorPacket(int color)
        implements CustomPacketPayload {

    public static final Type<FlashlightColorPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            NovaxLights.MODID,
                            "flashlight_color"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            FlashlightColorPacket
            > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            FlashlightColorPacket::color,
            FlashlightColorPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            FlashlightColorPacket packet,
            IPayloadContext context
    ) {

        context.enqueueWork(() -> {

            ServerPlayer player =
                    (ServerPlayer) context.player();

            ItemStack stack =
                    player.getMainHandItem();

            stack.set(
                    DataComponents.DYED_COLOR,
                    new DyedItemColor(packet.color(), false)
            );
        });
    }
}