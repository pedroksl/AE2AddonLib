package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

public class AddonItems {

    static AddonItems INSTANCE;
    private final DeferredRegister.Items DR;

    public AddonItems(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonItems on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.createItems(modId);
    }

    private static AddonItems getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Item Registration is not initialized.");
        }
        return INSTANCE;
    }

    static DeferredRegister.Items getDR() {
        return getInstance().DR;
    }

    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    protected static <T extends Item> ItemDefinition<T> item(
            String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, INSTANCE.DR.registerItem(id, factory));
        ITEMS.add(definition);
        return definition;
    }

    protected static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
