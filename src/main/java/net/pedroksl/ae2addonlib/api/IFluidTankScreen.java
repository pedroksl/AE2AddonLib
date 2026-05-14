package net.pedroksl.ae2addonlib.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
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

    /**
     * Plays sounds depending on if the fluid was inserted or extracted.
     * @param isInsert If the fluid was inserted or extracted.
     */
    static void playDownSound(boolean isInsert) {
        var handler = Minecraft.getInstance().getSoundManager();
        if (isInsert) {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F));
        } else {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_FILL, 1.0F, 1.0F));
        }
    }
}
