package net.pedroksl.ae2addonlib.api;

import net.neoforged.neoforge.fluids.FluidStack;

public interface IFluidTankScreen {

    void updateFluidTankContents(int index, FluidStack stack);

    void playSoundFeedback(boolean isInsert);
}
