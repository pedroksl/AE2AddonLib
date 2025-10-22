package net.pedroksl.ae2addonlib.api;

import net.neoforged.neoforge.fluids.FluidStack;

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
}
