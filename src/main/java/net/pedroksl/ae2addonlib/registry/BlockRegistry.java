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

public class BlockRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister.Blocks> DRMap = new HashMap<>();
    private static final Map<String, List<BlockDefinition<?>>> BLOCKS = new HashMap<>();
    private final String modId;

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

    public List<BlockDefinition<?>> getBlocks() {
        return getBlocks(this.modId);
    }

    public static List<BlockDefinition<?>> getBlocks(String modId) {
        return Collections.unmodifiableList(BLOCKS.get(modId));
    }

    protected static <T extends Block> BlockDefinition<T> block(
            String modId, String englishName, String id, Supplier<T> blockSupplier) {
        return block(modId, englishName, id, blockSupplier, null);
    }

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

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
