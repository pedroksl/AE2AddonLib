package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.ae2addonlib.registry.helpers.CreativeTabItem;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

public class CreativeTabRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<CreativeModeTab>> DRMap = new HashMap<>();
    private final String modId;

    public CreativeTabRegistry(String modId, Component title, Supplier<ItemStack> icon) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize CreativeTabRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        var dr = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
        dr.register("tab", () -> CreativeModeTab.builder()
                .title(title)
                .icon(icon)
                .displayItems((p, o) -> CreativeTabRegistry.populateTab(modId, p, o))
                .build());

        DRMap.put(modId, dr);
    }

    static DeferredRegister<CreativeModeTab> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    private static void populateTab(
            String modId, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.addAll(ItemRegistry.getItems(modId));
        itemDefs.addAll(BlockRegistry.getBlocks(modId).stream()
                .map(BlockDefinition::item)
                .toList());
        itemDefs.addAll(FluidRegistry.getFluids(modId).stream()
                .map(FluidDefinition::bucketItemId)
                .toList());

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(params, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(params, output);
            } else if (item instanceof CreativeTabItem creativeTabItem) {
                creativeTabItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(itemDef);
            }
        }
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
