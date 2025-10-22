package net.pedroksl.ae2addonlib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

/**
 * Handles the registration of network packets. Provides helper functions to make the registration process easier.
 * The recommended way to use this class is to extend it with a static class and override {@link #onRegister(PayloadRegistrar)}
 * to add calls to register the mod's packets.
 */
public abstract class NetworkHandler {

    private final String modId;

    /**
     * Constructs the handler saving the modId for future use.
     * @param modId The MOD_ID of the extender mod.
     */
    public NetworkHandler(String modId) {
        this.modId = modId;
    }

    /**
     * The {@link RegisterPayloadHandlersEvent} handler. This method should be added as a listener in the main mod class.
     * @param event The event to be handled.
     */
    public final void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(this.modId);

        onRegister(registrar);
    }

    /**
     * Entry point for addon packet registration. Override this and call the register methods inside.
     * Usage examle: {@link LibNetworkHandler#onRegister(PayloadRegistrar)}.
     * @param registrar The mod's registrar. It's needed for the register methods.
     */
    public abstract void onRegister(PayloadRegistrar registrar);

    /**
     * Registers client-bound packets. These packets should be created as records that implement the {@link ClientboundPacket} interface.
     * @param registrar The registrar, used to register the packet.
     * @param type The packet's type. Constructed using {@link CustomAppEngPayload#createType(String)}.
     * @param codec The {@link StreamCodec} that encodes/decodes the packet.
     * @param <T> The packet's class.
     */
    protected static <T extends ClientboundPacket> void clientbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
    }

    /**
     * Registers server-bound packets. These packets should be created as records that implement the {@link ServerboundPacket} interface.
     * @param registrar The registrar, used to register the packet.
     * @param type The packet's type. Constructed using {@link CustomAppEngPayload#createType(String)}.
     * @param codec The {@link StreamCodec} that encodes/decodes the packet.
     * @param <T> The packet's class.
     */
    protected static <T extends ServerboundPacket> void serverbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
    }

    /**
     * Registers bidirectional packets. These packets should be created as records that implement both {@link ServerboundPacket} and {@link ClientboundPacket} interfaces.
     * @param registrar The registrar, used to register the packet.
     * @param type The packet's type. Constructed using {@link CustomAppEngPayload#createType(String)}.
     * @param codec The {@link StreamCodec} that encodes/decodes the packet.
     * @param <T> The packet's class.
     */
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
