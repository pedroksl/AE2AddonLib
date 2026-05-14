package net.pedroksl.ae2addonlib.client;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

import appeng.core.network.ClientboundPacket;

public abstract class ClientNetworkHandler {
    /**
     * Entry point for addon packet registration. Override this and call the register methods inside.
     * Usage example: {@link LibClientNetworkHandler#registerPackets(RegisterClientPayloadHandlersEvent)}.
     * @param event The Payload Register Event.
     */
    public abstract void registerPackets(RegisterClientPayloadHandlersEvent event);

    protected static <T extends ClientboundPacket> void register(
            RegisterClientPayloadHandlersEvent event,
            CustomPacketPayload.Type<@NotNull T> type,
            ClientNetworkHandler.ClientPacketHandler<T> handler) {
        event.register(type, (payload, context) -> handler.handle(payload, Minecraft.getInstance(), context.player()));
    }

    @FunctionalInterface
    protected interface ClientPacketHandler<T extends ClientboundPacket> {
        void handle(T payload, Minecraft minecraft, Player player);
    }
}
