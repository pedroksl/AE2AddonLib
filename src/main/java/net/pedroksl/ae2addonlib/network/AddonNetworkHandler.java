package net.pedroksl.ae2addonlib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pedroksl.ae2addonlib.network.clientPacket.FluidTankClientAudioPacket;
import net.pedroksl.ae2addonlib.network.clientPacket.FluidTankStackUpdatePacket;
import net.pedroksl.ae2addonlib.network.clientPacket.OutputDirectionUpdatePacket;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonConfigButtonPacket;
import net.pedroksl.ae2addonlib.network.serverPacket.FluidTankItemUsePacket;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.ServerboundPacket;

public abstract class AddonNetworkHandler {

    private final String modId;

    public AddonNetworkHandler(String modId) {
        this.modId = modId;
    }

    public void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(this.modId);

        try {
            clientbound(registrar, FluidTankClientAudioPacket.TYPE, FluidTankClientAudioPacket.STREAM_CODEC);
            clientbound(registrar, FluidTankStackUpdatePacket.TYPE, FluidTankStackUpdatePacket.STREAM_CODEC);
            clientbound(registrar, OutputDirectionUpdatePacket.TYPE, OutputDirectionUpdatePacket.STREAM_CODEC);
            serverbound(registrar, AddonConfigButtonPacket.TYPE, AddonConfigButtonPacket.STREAM_CODEC);
            serverbound(registrar, FluidTankItemUsePacket.TYPE, FluidTankItemUsePacket.STREAM_CODEC);
        } catch (UnsupportedOperationException e) {
            // Lib packets already registered, ignore this step
        }

        onRegister(registrar);
    }

    public abstract void onRegister(PayloadRegistrar registrar);

    protected static <T extends ClientboundPacket> void clientbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
    }

    protected static <T extends ServerboundPacket> void serverbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
    }

    protected static <T extends ServerboundPacket & ClientboundPacket> void bidirectional(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playBidirectional(type, codec, (payload, context) -> {
            if (context.flow().isClientbound()) {
                payload.handleOnClient(context);
            } else if (context.flow().isServerbound()) {
                payload.handleOnServer(context);
            }
        });
    }
}
