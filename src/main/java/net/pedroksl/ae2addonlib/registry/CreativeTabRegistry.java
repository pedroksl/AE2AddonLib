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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;
import net.pedroksl.ae2addonlib.registry.helpers.ICreativeTabItem;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.items.AEBaseItem;

/**
 * <p>Class responsible for the registering of creative tab items.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class CreativeTabRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<CreativeModeTab>> DRMap = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register.
     * @param modId The MOD_ID of the mod creating this instance.
     * @param title The {@link Component} of the title of the tab.
     * @param icon A supplier of the {@link ItemStack} to be used as the tab icon.
     */
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

    /**
     * Convenience method that will look for the implementations of #addToMainCreativeTab or their overrides for every
     * registered item, block or fluid. If an item doesn't extend from {@link AEBaseItem}, it can be marked with
     * {@link ICreativeTabItem} to add their own implementation of #addToMainCreativeTab.
     */
    private static void populateTab(
            String modId, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<LibItemDefinition<?>>();
        itemDefs.addAll(ItemRegistry.getItems(modId));
        itemDefs.addAll(BlockRegistry.getBlocks(modId).stream()
                .map(LibBlockDefinition::item)
                .toList());
        itemDefs.addAll(FluidRegistry.getFluids(modId).stream()
                .map(FluidDefinition::bucketItemId)
                .toList());

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
            } else if (item instanceof ICreativeTabItem creativeTabItem) {
                creativeTabItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(itemDef);
            }
        }
    }

    /**
     * Used to finalize the creative tab registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
