package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.BlockDefinition;

/**
 * Container class for block registration.
 * @param <T> Class that extends {@link Block}.
 */
public class LibBlockDefinition<T extends Block> implements ItemLike {

    private final String englishName;
    private final RegistryObject<T> block;
    private final LibItemDefinition<BlockItem> item;

    /**
     * Constructs a block definition using the name, registry object of the block and the definition of the related item.
     * @param englishName Human-readable english name of the block.
     * @param block The registry object of the block.
     * @param item The item definition of the related item.
     */
    public LibBlockDefinition(String englishName, RegistryObject<T> block, LibItemDefinition<BlockItem> item) {
        this.englishName = englishName;
        this.item = Objects.requireNonNull(item, "item");
        this.block = Objects.requireNonNull(block, "block");
    }

    /**
     * Getter for the english name of this block.
     * @return Human-readable name of this block.
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * Getter for the {@link ResourceLocation} of this block.
     * @return The resource location.
     */
    public ResourceLocation id() {
        return block.getId();
    }

    /**
     * Getter for the block.
     * @return The block.
     */
    public final T block() {
        return this.block.get();
    }

    /**
     * Converts this block into an {@link ItemStack}.
     * @return An item stack representing this block.
     */
    public ItemStack stack() {
        return item.stack();
    }

    /**
     * Converts this block to an {@link ItemStack} with a desired amount.
     * @param stackSize The stack size.
     * @return An item stack representing this block with the desired stack size.
     */
    public ItemStack stack(int stackSize) {
        return item.stack(stackSize);
    }

    /**
     * Wraps this block inside a {@link GenericStack} with a desired amount.
     * @param stackSize The stack size.
     * @return A generic stack representing this block with the desired stack size.
     */
    public GenericStack genericStack(long stackSize) {
        return item.genericStack(stackSize);
    }

    /**
     * Getter for the block holder.
     * @return The block holder.
     */
    public Holder<T> holder() {
        return block.getHolder().orElseThrow();
    }

    /**
     * Compares this block's item with another {@link ItemStack}.
     * @param comparableStack The stack to compare to.
     * @return If the items are the same type of stack.
     */
    public boolean is(ItemStack comparableStack) {
        return item.is(comparableStack);
    }

    /**
     * Compares this block's item with another {@link AEKey}.
     * @param key The key to compare to.
     * @return If the items are the same key.
     */
    public boolean is(AEKey key) {
        return item.is(key);
    }

    /**
     * Getter for the item definition of this block's item.
     * @return The item definition.
     */
    public LibItemDefinition<BlockItem> item() {
        return item;
    }

    @Override
    public @NotNull BlockItem asItem() {
        return item.asItem();
    }

    /**
     * Converts this definition into an AE2's {@link BlockDefinition}.
     * @return A block definition of this block.
     */
    public BlockDefinition<T> getBlockDefinition() {
        return new BlockDefinition<>(this.englishName, id(), block(), asItem());
    }
}
