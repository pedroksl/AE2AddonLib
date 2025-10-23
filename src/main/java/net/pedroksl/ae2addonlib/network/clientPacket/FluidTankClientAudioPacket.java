package net.pedroksl.ae2addonlib.network.clientPacket;

/**
 * Record used to define the packet used to tell the client to trigger sound from a {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot} interaction.
 * @param isInsert Defines if the sound to be played is of an insertion or extraction.
 */
public record FluidTankClientAudioPacket(boolean isInsert) { // implements ClientboundPacket {

    // @Override
    // public void handleOnClient(IPayloadContext context) {
    //    if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
    //        FluidTankSlot.playDownSound(isInsert);
    //    }
    // }
}
