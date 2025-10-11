package net.pedroksl.ae2addonlib.registry.helpers;

import net.minecraft.world.item.CreativeModeTab;

public interface CreativeTabItem {

    void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output);
}
