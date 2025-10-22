package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;

/**
 * <p>Class responsible for the registering of blocks.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class BlockRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister.Blocks> DRMap = new HashMap<>();
    private static final Map<String, List<BlockDefinition<?>>> BLOCKS = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and BLOCKS map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public BlockRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize BlockRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.createBlocks(modId));
        BLOCKS.put(modId, new ArrayList<>());
    }

    static DeferredRegister.Blocks getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    /**
     * Non-static version of {@link #getBlocks(String)}.
     * @return A list containing all registered {@link BlockDefinition}s.
     */
    public List<BlockDefinition<?>> getBlocks() {
        return getBlocks(this.modId);
    }

    /**
     * Helper method to create a collection of all registered blocks.
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link BlockDefinition}s.
     */
    public static List<BlockDefinition<?>> getBlocks(String modId) {
        return Collections.unmodifiableList(BLOCKS.getOrDefault(modId, new ArrayList<>()));
    }

    /**
     * Overload of {@link #block(String, String, String, Supplier, BiFunction)} that passes in a null item factory
     * for simle blocks that use the default {@link BlockItem}.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alonside {@link #getBlocks()}.
     * @param id The id of the registered block.
     * @param blockSupplier The constructor of the block.
     * @param <T> Block class that extended {@link Block}.
     * @return The {@link BlockDefinition} containing all relevant information for this block.
     */
    protected static <T extends Block> BlockDefinition<T> block(
            String modId, String englishName, String id, Supplier<T> blockSupplier) {
        return block(modId, englishName, id, blockSupplier, null);
    }

    /**
     * Complete block registration method for blocks with a custom item.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getBlocks()}.
     * @param id The id of the registered block.
     * @param blockSupplier The constructor of the block.
     * @param itemFactory The item construction factory.
     * @param <T> Block class that extends {@link Block}.
     * @return The {@link BlockDefinition} containing all relevant information for this block.
     */
    protected static <T extends Block> BlockDefinition<T> block(
            String modId,
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var deferredBlock = getDR(modId).register(id, blockSupplier);
        var deferredItem = ItemRegistry.getDR(modId).register(id, () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, new Item.Properties());
                if (item == null) {
                    var rl = ResourceLocation.fromNamespaceAndPath(getDR(modId).getNamespace(), id);
                    throw new IllegalArgumentException("BlockItem factory for " + rl + " return null");
                }
                return item;
            } else if (block instanceof AEBaseBlock) {
                return new AEBaseBlockItem(block, itemProperties);
            } else {
                return new BlockItem(block, itemProperties);
            }
        });

        var definition =
                new BlockDefinition<>(englishName, deferredBlock, new ItemDefinition<>(englishName, deferredItem));
        BLOCKS.get(modId).add(definition);
        return definition;
    }

    /**
     * Used to finalize the block registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
