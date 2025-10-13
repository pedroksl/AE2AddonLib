package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

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

public class FluidRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<FluidType>> DR_FLUID_TYPES = new HashMap<>();
    private static final Map<String, DeferredRegister<Fluid>> DR_FLUIDS = new HashMap<>();
    private static final Map<String, DeferredRegister.Blocks> DR_FLUID_BLOCKS = new HashMap<>();
    private static final Map<String, DeferredRegister.Items> DR_BUCKET_ITEMS = new HashMap<>();
    private static final Map<String, List<FluidDefinition<?, ?>>> FLUIDS = new HashMap();
    private final String modId;

    public FluidRegistry(String modId) {
        if (DR_FLUID_TYPES.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize FluidRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DR_FLUID_TYPES.put(modId, DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId));
        DR_FLUIDS.put(modId, DeferredRegister.create(Registries.FLUID, modId));
        DR_FLUID_BLOCKS.put(modId, DeferredRegister.createBlocks(modId));
        DR_BUCKET_ITEMS.put(modId, DeferredRegister.createItems(modId));
        FLUIDS.put(modId, new ArrayList<>());
    }

    public List<FluidDefinition<?, ?>> getFluids() {
        return getFluids(this.modId);
    }

    public static List<FluidDefinition<?, ?>> getFluids(String modId) {
        return Collections.unmodifiableList(FLUIDS.get(modId));
    }

    protected static <F extends Fluid, B extends LiquidBlock> FluidDefinition<F, B> fluid(
            String modId,
            String englishName,
            String id,
            Supplier<FluidType> fluidTypeSupplier,
            Supplier<F> flowingSupplier,
            Supplier<F> sourceSupplier,
            Supplier<B> liquidBlockSupplier) {
        var type = DR_FLUID_TYPES.get(modId).register(id + "_type", fluidTypeSupplier);
        var flowing = DR_FLUIDS.get(modId).register(id + "_flowing", flowingSupplier);
        var source = DR_FLUIDS.get(modId).register(id + "_source", sourceSupplier);
        var block = DR_FLUID_BLOCKS.get(modId).register(id + "_block", liquidBlockSupplier);
        var bucketItem = DR_BUCKET_ITEMS
                .get(modId)
                .register(
                        id + "_bucket",
                        () -> new BucketItem(
                                source.get(),
                                new Item.Properties()
                                        .craftRemainder(Items.BUCKET)
                                        .stacksTo(1)));

        var bucketDefinition = new ItemDefinition<>(englishName + " Bucket", bucketItem);
        var definition = new FluidDefinition<>(englishName, type, flowing, source, block, bucketDefinition);

        FLUIDS.get(modId).add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        DR_FLUID_TYPES.get(this.modId).register(eventBus);
        DR_FLUIDS.get(this.modId).register(eventBus);
        DR_FLUID_BLOCKS.get(this.modId).register(eventBus);
        DR_BUCKET_ITEMS.get(this.modId).register(eventBus);
    }
}
