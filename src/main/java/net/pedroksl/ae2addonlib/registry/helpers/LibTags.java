package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.BiFunction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

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

    /**
     * Helper function to get an int tag with a default value return for non-existent tags.
     * @param stack The stack to read tags from.
     * @param tagId The id of the tag to read.
     * @param defaultValue The default value to return for non-existent tags.
     * @return The tag value or the default value.
     */
    public static int getIntOrDefault(ItemStack stack, String tagId, int defaultValue) {
        return getTagOrDefault(stack, tagId, defaultValue, CompoundTag::getInt);
    }

    /**
     * Helper function to get an int tag with a default value return for non-existent tags.
     * @param stack The stack to read tags from.
     * @param tagId The id of the tag to read.
     * @param defaultValue The default value to return for non-existent tags.
     * @return The tag value or the default value.
     */
    public static boolean getBooleanOrDefault(ItemStack stack, String tagId, boolean defaultValue) {
        return getTagOrDefault(stack, tagId, defaultValue, CompoundTag::getBoolean);
    }

    /**
     * Generic helper function to get a tag with a default value return for non-existent tags.
     * @param stack The stack to read tags from.
     * @param tagId The id of the tag to read.
     * @param defaultValue The default value to return for non-existent tags.
     * @param getter Bi function called when reading the tag for the desired value type.
     * @param <V> Class of the value returned.
     * @return The tag value or the default value.
     */
    public static <V> V getTagOrDefault(
            ItemStack stack, String tagId, V defaultValue, BiFunction<CompoundTag, String, V> getter) {
        var tag = stack.getOrCreateTag();
        if (tag.contains(tagId)) {
            return getter.apply(tag, tagId);
        } else {
            return defaultValue;
        }
    }
}
