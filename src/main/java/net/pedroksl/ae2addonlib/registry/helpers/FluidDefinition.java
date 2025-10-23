package net.pedroksl.ae2addonlib.registry.helpers;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;

/**
 * Container record for fluid registration.
 * @param englishName Human-readable name of the fluid.
 * @param fluidTypeRegistry The holder of the {@link FluidType}.
 * @param flowingRegistry The holder of the flowing fluid.
 * @param sourceRegistry The holder of the source fluid.
 * @param blockRegistry The holder of the fluid block.
 * @param bucketItemId The {@link LibItemDefinition} of the bucket item.
 * @param <F> Class that extends a {@link Fluid}.
 * @param <B> Class that extends a {@link LiquidBlock}
 */
@ParametersAreNonnullByDefault
public record FluidDefinition<F extends Fluid, B extends LiquidBlock>(
        String englishName,
        RegistryObject<FluidType> fluidTypeRegistry,
        RegistryObject<F> flowingRegistry,
        RegistryObject<F> sourceRegistry,
        RegistryObject<B> blockRegistry,
        LibItemDefinition<BucketItem> bucketItemId) {

    /**
     * Getter function.
     * @return The resource location of the source fluid.
     */
    public ResourceLocation id() {
        return this.sourceRegistry.getId();
    }

    public final Holder<FluidType> fluidTypeId() {
        return this.fluidTypeRegistry.getHolder().orElseThrow();
    }

    /**
     * Getter function.
     * @return The fluid's {@link FluidType}.
     */
    public FluidType fluidType() {
        return this.fluidTypeRegistry.get();
    }

    public final Holder<F> flowingId() {
        return this.flowingRegistry.getHolder().orElseThrow();
    }

    /**
     * Getter function.
     * @return The flowing fluid.
     */
    public F flowing() {
        return this.flowingRegistry.get();
    }

    public final Holder<F> sourceId() {
        return this.sourceRegistry.getHolder().orElseThrow();
    }

    /**
     * Getter function.
     * @return The source fluid.
     */
    public F source() {
        return this.sourceRegistry.get();
    }

    public final ResourceLocation blockResource() {
        return this.blockRegistry.getId();
    }

    public final Holder<B> blockId() {
        return this.blockRegistry.getHolder().orElseThrow();
    }

    /**
     * Getter function.
     * @return The fluid block.
     */
    public B block() {
        return this.blockRegistry.get();
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
        return new FluidStack(this.sourceRegistry.get(), 1000);
    }

    /**
     * Creates a {@link FluidStack} containing a specified volume of this fluid.
     * @param amount The desired amount for the fluid stack.
     * @return The fluid stack.
     */
    public FluidStack stack(int amount) {
        return new FluidStack(this.sourceRegistry.get(), amount);
    }
}
