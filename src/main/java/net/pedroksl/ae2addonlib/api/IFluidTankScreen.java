package net.pedroksl.ae2addonlib.api;

import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot;

/**
 * Marks a screen as compatible with {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot}.
 */
public interface IFluidTankScreen {

    /**
     * Method called whenever a tank was interacted with and needs to update.
     * @param index The tank index.
     * @param stack The tank's updated stack.
     */
    void updateFluidTankContents(int index, FluidStack stack);

    /**
     * Method called when a successful interaction wants to play a sound on the client.
     * @param isInsert Decides if the sound is of an insertion or extraction.
     */
    default void playDownSound(boolean isInsert) {
        FluidTankSlot.playDownSound(isInsert);
    }
}
