package com.novaxzx.novaxlights.network;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.entity.AreaLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AreaLightPacket(BlockPos pos, int action, String value)
        implements CustomPacketPayload {

    public static final Type<AreaLightPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            NovaxLights.MODID,
                            "area_light_packet"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, AreaLightPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeBlockPos(packet.pos);
                        buf.writeInt(packet.action);
                        buf.writeUtf(packet.value);
                    },
                    buf -> new AreaLightPacket(
                            buf.readBlockPos(),
                            buf.readInt(),
                            buf.readUtf()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            AreaLightPacket packet,
            IPayloadContext context
    ) {

        context.enqueueWork(() -> {

            BlockEntity be =
                    context.player()
                            .level()
                            .getBlockEntity(packet.pos);

            if(be instanceof AreaLightBlockEntity light) {

                switch(packet.action) {

                    case 0 -> light.setDistanceFromText(packet.value);
                    case 1 -> light.setBrightnessFromText(packet.value);
                    case 2 -> light.setHexColor(packet.value);
                    case 3 -> light.setAngleFromText(packet.value);
                    case 4 -> light.setSizeXFromText(packet.value);
                    case 5 -> light.setSizeYFromText(packet.value);
                }
            }
        });
    }
}