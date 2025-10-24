package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.ItemDefinition;
import appeng.util.helpers.ItemComparisonHelper;

/**
 * Container class for item registration.
 * @param <T> Class that extends {@link Item}.
 */
public class LibItemDefinition<T extends Item> implements ItemLike, Supplier<T> {

    private final String englishName;
    private final RegistryObject<T> item;

    /**
     * Constructs an item definition using the name and registry.
     * @param englishName Human-readable name of this item.
     * @param item The registry object of this item.
     */
    public LibItemDefinition(String englishName, RegistryObject<T> item) {
        this.englishName = englishName;
        this.item = item;
    }

    /**
     * Getter for the english name of this item.
     * @return Human-readable name of this item.
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * Getter for the {@link ResourceLocation} of this item.
     * @return The resource location.
     */
    public ResourceLocation id() {
        return this.item.getId();
    }

    /**
     * Converts this item to an {@link ItemStack}.
     * @return An item stack representing this item.
     */
    public ItemStack stack() {
        return stack(1);
    }

    /**
     * Converts this item to an {@link ItemStack} with a desired amount.
     * @param stackSize The stack size.
     * @return An item stack representing this item with the desired stack size.
     */
    public ItemStack stack(int stackSize) {
        return new ItemStack(item.get(), stackSize);
    }

    /**
     * Wraps this item inside a {@link GenericStack} with a desired amount.
     * @param stackSize The stack size.
     * @return A generic stack representing this item with the desired stack size.
     */
    public GenericStack genericStack(long stackSize) {
        return new GenericStack(AEItemKey.of(item.get()), stackSize);
    }

    /**
     * Getter for the item holder.
     * @return The item holder.
     */
    public Holder<T> holder() {
        return item.getHolder().orElseThrow();
    }

    /**
     * Compares this item definition with another {@link ItemStack}.
     * @param comparableStack The stack to compare to.
     * @return If the items are the same type of stack.
     */
    @Deprecated(forRemoval = true, since = "1.21")
    public final boolean isSameAs(ItemStack comparableStack) {
        return is(comparableStack);
    }

    /**
     * Compares this item definition with another {@link ItemStack}.
     * @param comparableStack The stack to compare to.
     * @return If the items are the same type of stack.
     */
    public final boolean is(ItemStack comparableStack) {
        return ItemComparisonHelper.isEqualItemType(comparableStack, this.stack());
    }

    /**
     * Compares this item definition with another {@link AEKey}.
     * @param key The key to compare to.
     * @return If the items are the same key.
     */
    public final boolean is(AEKey key) {
        if (key instanceof AEItemKey itemKey) {
            return asItem() == itemKey.getItem();
        }
        return false;
    }

    /**
     * Compares this item definition with another {@link AEKey}.
     * @param key The key to compare to.
     * @return If the items are the same key.
     */
    public final boolean isSameAs(AEKey key) {
        return is(key);
    }

    @Override
    public T get() {
        return item.get();
    }

    @Override
    public T asItem() {
        return item.get();
    }

    /**
     * Converts this definition into an AE2's {@link ItemDefinition}.
     * @return An item definition of this item.
     */
    public ItemDefinition<T> getItemDefinition() {
        return new ItemDefinition<>(this.englishName, id(), asItem());
    }
}
