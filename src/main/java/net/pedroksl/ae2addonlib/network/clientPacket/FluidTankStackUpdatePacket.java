package net.pedroksl.ae2addonlib.network.clientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

/**
 * Class used to define a packet used to update the client's {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot} with
 * the appropriate amount of fluid,
 */
public class FluidTankStackUpdatePacket extends AddonPacket {

    private final int index;
    private final FluidStack stack;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public FluidTankStackUpdatePacket(FriendlyByteBuf stream) {
        this.index = stream.readInt();
        this.stack = stream.readFluidStack();
    }

    /**
     * Constructs the packet to send to the stream.
     * @param index The tank slot index.
     * @param stack The tank's current {@link FluidStack}.
     */
    public FluidTankStackUpdatePacket(int index, FluidStack stack) {
        this.index = index;
        this.stack = stack;
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeInt(index);
        stream.writeFluidStack(stack);
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.updateFluidTankContents(index, stack);
        }
    }
}
