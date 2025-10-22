package net.pedroksl.ae2addonlib.registry.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import appeng.core.definitions.ItemDefinition;

/**
 * Container record for fluid registration.
 * @param englishName Human-readable name of the fluid.
 * @param fluidTypeHolder The holder of the {@link FluidType}.
 * @param flowingHolder The holder of the flowing fluid.
 * @param sourceHolder The holder of the source fluid.
 * @param blockHolder The holder of the fluid block.
 * @param bucketItemId THe {@link ItemDefinition} of the bucket item.
 * @param <F> Class that extends a {@link Fluid}.
 * @param <B> Class that extends a {@link LiquidBlock}
 */
public record FluidDefinition<F extends Fluid, B extends LiquidBlock>(
        String englishName,
        DeferredHolder<FluidType, FluidType> fluidTypeHolder,
        DeferredHolder<Fluid, F> flowingHolder,
        DeferredHolder<Fluid, F> sourceHolder,
        DeferredHolder<Block, B> blockHolder,
        ItemDefinition<BucketItem> bucketItemId) {
    /**
     * Getter function.
     * @return The resource location of the source fluid.
     */
    public ResourceLocation id() {
        return this.sourceHolder.getId();
    }

    /**
     * Getter function.
     * @return The fluid's {@link FluidType}.
     */
    public FluidType fluidType() {
        return this.fluidTypeHolder.get();
    }

    /**
     * Getter function.
     * @return The flowing fluid.
     */
    public F flowing() {
        return this.flowingHolder.get();
    }

    /**
     * Getter function.
     * @return The source fluid.
     */
    public F source() {
        return this.sourceHolder.get();
    }

    /**
     * Getter function.
     * @return The fluid block.
     */
    public B block() {
        return this.blockHolder.get();
    }

    /**
     * Getter function.
     * @return The fluid's bucket item.
     */
    public BucketItem bucketItem() {
        return this.bucketItemId.get();
    }

    /**
     * Creates a {@link FluidStack} containing a bucket volume of this fluid.
     * @return Fluid stack containing 1000mB.
     */
    public FluidStack stack() {
        return new FluidStack(this.sourceHolder.get(), 1000);
    }

    /**
     * Creates a {@link FluidStack} containing a specified volume of this fluid.
     * @param amount The desired amount for the fluid stack.
     * @return The fluid stack.
     */
    public FluidStack stack(int amount) {
        return new FluidStack(this.sourceHolder.get(), amount);
    }
}
