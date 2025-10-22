package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.api.IFluidTankHandler;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

/**
 * Record used to define the packet used to tell the server that the player is interacting with a {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot}.
 * @param index The slot index
 * @param button The button used in the interaction.
 */
public record FluidTankItemUsePacket(int index, int button) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankItemUsePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    FluidTankItemUsePacket::index,
                    ByteBufCodecs.INT,
                    FluidTankItemUsePacket::button,
                    FluidTankItemUsePacket::new);

    public static final Type<FluidTankItemUsePacket> TYPE = CustomAppEngPayload.createType("ae2lib_fluid_tank_packet");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof IFluidTankHandler handler) {
            handler.onItemUse(index, button);
        }
    }
}
