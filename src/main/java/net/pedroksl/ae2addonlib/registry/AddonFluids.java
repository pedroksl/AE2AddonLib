package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

import appeng.core.definitions.ItemDefinition;

public class AddonFluids {

    static AddonFluids INSTANCE;
    private final DeferredRegister<FluidType> DR_FLUID_TYPES;
    private final DeferredRegister<Fluid> DR_FLUIDS;
    private final DeferredRegister.Blocks DR_FLUID_BLOCKS;
    private final DeferredRegister.Items DR_BUCKET_ITEMS;

    public AddonFluids(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonFluids on Client Dist.");
        }

        INSTANCE = this;

        DR_FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId);
        DR_FLUIDS = DeferredRegister.create(Registries.FLUID, modId);
        DR_FLUID_BLOCKS = DeferredRegister.createBlocks(modId);
        DR_BUCKET_ITEMS = DeferredRegister.createItems(modId);
    }

    private static AddonFluids getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Fluid Registration is not initialized.");
        }
        return INSTANCE;
    }

    private static final List<FluidDefinition<?, ?>> FLUIDS = new ArrayList<>();

    public static List<FluidDefinition<?, ?>> getFluids() {
        return Collections.unmodifiableList(FLUIDS);
    }

    protected static <F extends Fluid, B extends LiquidBlock> FluidDefinition<F, B> fluid(
            String englishName,
            String id,
            Supplier<FluidType> fluidTypeSupplier,
            Supplier<F> flowingSupplier,
            Supplier<F> sourceSupplier,
            Supplier<B> liquidBlockSupplier) {
        var instance = getInstance();
        var type = instance.DR_FLUID_TYPES.register(id + "_type", fluidTypeSupplier);
        var flowing = instance.DR_FLUIDS.register(id + "_flowing", flowingSupplier);
        var source = instance.DR_FLUIDS.register(id + "_source", sourceSupplier);
        var block = instance.DR_FLUID_BLOCKS.register(id + "_block", liquidBlockSupplier);
        var bucketItem = instance.DR_BUCKET_ITEMS.register(
                id + "_bucket",
                () -> new BucketItem(
                        source.get(),
                        new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

        var bucketDefinition = new ItemDefinition<>(englishName + " Bucket", bucketItem);
        var definition = new FluidDefinition<>(englishName, type, flowing, source, block, bucketDefinition);

        FLUIDS.add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        var instance = getInstance();
        instance.DR_FLUID_TYPES.register(eventBus);
        instance.DR_FLUIDS.register(eventBus);
        instance.DR_FLUID_BLOCKS.register(eventBus);
        instance.DR_BUCKET_ITEMS.register(eventBus);
    }
}
