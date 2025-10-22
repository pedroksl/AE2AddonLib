package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

import appeng.core.definitions.ItemDefinition;

/**
 * <p>Class responsible for the registering of fluids.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class FluidRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<FluidType>> DR_FLUID_TYPES = new HashMap<>();
    private static final Map<String, DeferredRegister<Fluid>> DR_FLUIDS = new HashMap<>();
    private static final Map<String, DeferredRegister.Blocks> DR_FLUID_BLOCKS = new HashMap<>();
    private static final Map<String, DeferredRegister.Items> DR_BUCKET_ITEMS = new HashMap<>();
    private static final Map<String, List<FluidDefinition<?, ?>>> FLUIDS = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and BER map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
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

    /**
     * Non-static version of {@link #getFluids(String)}.
     * @return A list containing all registered {@link FluidDefinition}s.
     */
    public List<FluidDefinition<?, ?>> getFluids() {
        return getFluids(this.modId);
    }

    /**
     * Helper method to create a collection of all registered fluids
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link FluidDefinition}s.
     */
    public static List<FluidDefinition<?, ?>> getFluids(String modId) {
        return Collections.unmodifiableList(FLUIDS.getOrDefault(modId, new ArrayList<>()));
    }

    /**
     * <p>Registration method for fluids.</p>
     * This method needs definitions of the fluid in various different states.
     * For the {@link FluidType}, water-based fluids (that use the same base textures) can extend {@link net.pedroksl.ae2addonlib.util.WaterBasedFluidType}.
     * For the flowing and source suppliers, one option is to extend {@link net.neoforged.neoforge.fluids.BaseFlowingFluid}
     * and choose properties accordingly. The Liquid block supplier accepts an extension of {@link LiquidBlock}.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block.
     * Can be used in a language provider to generate translations alongside {@link #getFluids()}.
     * @param id The id of the registered block.
     * @param fluidTypeSupplier A {@link FluidType} supplier.
     * @param flowingSupplier A supplier of a flowing version of the fluid.
     * @param sourceSupplier A supplier of a source version of the fluid.
     * @param liquidBlockSupplier A supplier for the liquid block.
     * @param <F> A class that extends {@link Fluid}.
     * @param <B> A class that extends {@link LiquidBlock}.
     * @return A {@link FluidDefinition} containing all relevant information for this fluid.
     */
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

    /**
     * Used to finalize the fluid registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        DR_FLUID_TYPES.get(this.modId).register(eventBus);
        DR_FLUIDS.get(this.modId).register(eventBus);
        DR_FLUID_BLOCKS.get(this.modId).register(eventBus);
        DR_BUCKET_ITEMS.get(this.modId).register(eventBus);
    }

    /**
     * Helper method that can be used to register bucket coloring
     * @param stack The bucket item stack.
     * @param index The layer index.
     * @return The color as an int.
     */
    public static int getFluidColor(ItemStack stack, int index) {
        if (index == 1 && stack.getItem() instanceof BucketItem bucketItem) {
            return IClientFluidTypeExtensions.of(bucketItem.content).getTintColor();
        }
        return 0xFFFFFFFF;
    }
}
