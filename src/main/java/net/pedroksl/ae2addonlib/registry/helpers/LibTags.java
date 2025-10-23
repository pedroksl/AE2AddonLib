package net.pedroksl.ae2addonlib.registry.helpers;

import net.minecraft.nbt.CompoundTag;

/**
 * <p>Lib provided component set.</p>
 * This class contains commonly-used component types. Additional components will likely be added in the future.
 */
public final class LibTags {

    /**
     * A Simple NBT tag.
     * It's capable of holding a {@link CompoundTag}.
     */
    public static final String STACK_TAG = "generic_nbt";

    /**
     * An integer tag to save tint color data in an {@link net.minecraft.world.item.ItemStack}.
     */
    public static final String TINT_COLOR_TAG = "tint_color";
}
