package net.pedroksl.ae2addonlib.network.clientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record FluidTankClientAudioPacket(boolean isInsert) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankClientAudioPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, FluidTankClientAudioPacket::isInsert, FluidTankClientAudioPacket::new);

    public static final Type<FluidTankClientAudioPacket> TYPE =
            CustomAppEngPayload.createType("ae2lib_tank_audio_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(IPayloadContext context) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.playSoundFeedback(isInsert);
        }
    }
}
