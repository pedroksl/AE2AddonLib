package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

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

public class AddonBlocks {

    static AddonBlocks INSTANCE;
    private final DeferredRegister.Blocks DR;

    public AddonBlocks(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonBlocks on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.createBlocks(modId);
    }

    private static AddonBlocks getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Block Registration is not initialized.");
        }
        return INSTANCE;
    }

    static DeferredRegister.Blocks getDR() {
        return getInstance().DR;
    }

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    protected static <T extends Block> BlockDefinition<T> block(
            String englishName, String id, Supplier<T> blockSupplier) {
        return block(englishName, id, blockSupplier, null);
    }

    protected static <T extends Block> BlockDefinition<T> block(
            String englishName,
            String id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {
        var deferredBlock = getInstance().DR.register(id, blockSupplier);
        var deferredItem = AddonItems.getDR().register(id, () -> {
            var block = deferredBlock.get();
            var itemProperties = new Item.Properties();
            if (itemFactory != null) {
                var item = itemFactory.apply(block, new Item.Properties());
                if (item == null) {
                    var rl = ResourceLocation.fromNamespaceAndPath(getDR().getNamespace(), id);
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
        BLOCKS.add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
