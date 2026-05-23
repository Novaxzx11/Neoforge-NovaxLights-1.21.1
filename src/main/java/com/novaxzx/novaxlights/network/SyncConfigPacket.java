package com.novaxzx.novaxlights.network;

import com.novaxzx.novaxlights.NovaxLights;
import com.novaxzx.novaxlights.client.ClientConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncConfigPacket(

        int maxDistance,
        double maxBrightness,

        double maxXSize,
        double maxYSize,

        double maxAngle,

        boolean flashlightChangeColor,

        double flashlightBrightness,
        double flashlightDistance,
        double flashlightAngle,
        double flashlightSize,

        int flashlightDrainRate,
        int flashlightDamage

) implements CustomPacketPayload {

    public static final Type<SyncConfigPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            NovaxLights.MODID,
                            "sync_config"
                    )
            );

    public static final StreamCodec<ByteBuf, SyncConfigPacket> STREAM_CODEC =
            new StreamCodec<>() {

                @Override
                public SyncConfigPacket decode(ByteBuf buf) {

                    return new SyncConfigPacket(

                            buf.readInt(),
                            buf.readDouble(),

                            buf.readDouble(),
                            buf.readDouble(),

                            buf.readDouble(),

                            buf.readBoolean(),

                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),
                            buf.readDouble(),

                            buf.readInt(),
                            buf.readInt()
                    );
                }

                @Override
                public void encode(
                        ByteBuf buf,
                        SyncConfigPacket packet
                ) {

                    buf.writeInt(packet.maxDistance());

                    buf.writeDouble(packet.maxBrightness());

                    buf.writeDouble(packet.maxXSize());
                    buf.writeDouble(packet.maxYSize());

                    buf.writeDouble(packet.maxAngle());

                    buf.writeBoolean(packet.flashlightChangeColor());

                    buf.writeDouble(packet.flashlightBrightness());
                    buf.writeDouble(packet.flashlightDistance());
                    buf.writeDouble(packet.flashlightAngle());
                    buf.writeDouble(packet.flashlightSize());

                    buf.writeInt(packet.flashlightDrainRate());
                    buf.writeInt(packet.flashlightDamage());
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            SyncConfigPacket packet,
            IPayloadContext context
    ) {

        context.enqueueWork(() -> {

            ClientConfig.MAX_DISTANCE =
                    packet.maxDistance();

            ClientConfig.MAX_BRIGHTNESS =
                    packet.maxBrightness();

            ClientConfig.MAX_X_SIZE =
                    packet.maxXSize();

            ClientConfig.MAX_Y_SIZE =
                    packet.maxYSize();

            ClientConfig.MAX_ANGLE =
                    packet.maxAngle();

            ClientConfig.FLASHLIGHT_CHANGE_COLOR =
                    packet.flashlightChangeColor();

            ClientConfig.FLASHLIGHT_BRIGHTNESS =
                    packet.flashlightBrightness();

            ClientConfig.FLASHLIGHT_DISTANCE =
                    packet.flashlightDistance();

            ClientConfig.FLASHLIGHT_ANGLE =
                    packet.flashlightAngle();

            ClientConfig.FLASHLIGHT_SIZE =
                    packet.flashlightSize();

            ClientConfig.FLASHLIGHT_DRAIN_RATE =
                    packet.flashlightDrainRate();

            ClientConfig.FLASHLIGHT_DAMAGE =
                    packet.flashlightDamage();
        });
    }
}