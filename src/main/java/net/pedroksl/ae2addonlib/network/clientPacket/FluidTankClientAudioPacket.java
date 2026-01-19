package net.pedroksl.ae2addonlib.network.clientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

/**
 * Class used to define the packet used to tell the client to trigger sound from a {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot} interaction.
 */
public class FluidTankClientAudioPacket extends AddonPacket {

    private final boolean isInsert;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public FluidTankClientAudioPacket(FriendlyByteBuf stream) {
        this.isInsert = stream.readBoolean();
    }

    /**
     * Constructs the packet to send to the stream.
     * @param isInsert Defines if the sound to be played is of an insertion or extraction.
     */
    public FluidTankClientAudioPacket(boolean isInsert) {
        this.isInsert = isInsert;
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeBoolean(isInsert);
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.playDownSound(isInsert);
        }
    }
}
