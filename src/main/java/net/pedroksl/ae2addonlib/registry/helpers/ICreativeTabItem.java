package net.pedroksl.ae2addonlib.registry.helpers;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Interface used to mark items to be added to the creative tab.
 */
public interface ICreativeTabItem {

    /**
     * Adds the item to the creative tab.
     * @param parameters The item display parameters.
     * @param output The tab output.
     */
    void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output);
}
