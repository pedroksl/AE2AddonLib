package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.function.Supplier;

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

public class AddonCreativeTab {

    static AddonCreativeTab INSTANCE;
    private final DeferredRegister<CreativeModeTab> DR;

    public AddonCreativeTab(String modId, Component title, Supplier<ItemStack> icon) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize Creative Tab on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);

        DR.register("tab", () -> CreativeModeTab.builder()
                .title(title)
                .icon(icon)
                .displayItems(AddonCreativeTab::populateTab)
                .build());
    }

    private static AddonCreativeTab getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Item Registration is not initialized.");
        }
        return INSTANCE;
    }

    static DeferredRegister<CreativeModeTab> getDR() {
        return getInstance().DR;
    }

    private static void populateTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.addAll(AddonItems.getItems());
        itemDefs.addAll(
                AddonBlocks.getBlocks().stream().map(BlockDefinition::item).toList());
        itemDefs.addAll(AddonFluids.getFluids().stream()
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
        getDR().register(eventBus);
    }
}
