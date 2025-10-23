package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.api.IFluidTankHandler;
import net.pedroksl.ae2addonlib.network.AddonPacket;

/**
 * Class used to define the packet used to tell the server that the player is interacting with a {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot}.
 */
public class FluidTankItemUsePacket extends AddonPacket {

    private final int index;
    private final int button;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public FluidTankItemUsePacket(FriendlyByteBuf stream) {
        this.index = stream.readInt();
        this.button = stream.readInt();
    }

    /**
     * Constructs the packet to send to the stream.
     * @param index The slot index
     * @param button The button used in the interaction.
     */
    public FluidTankItemUsePacket(int index, int button) {
        this.index = index;
        this.button = button;
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeInt(index);
        stream.writeInt(button);
    }

    @Override
    public void serverPacketData(ServerPlayer serverPlayer) {
        if (serverPlayer.containerMenu instanceof IFluidTankHandler handler) {
            handler.onItemUse(index, button);
        }
    }
}
