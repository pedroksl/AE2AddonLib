package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Function;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

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

public class ItemRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister.Items> DRMap = new HashMap<>();
    private static final Map<String, List<ItemDefinition<?>>> ITEMS = new HashMap<>();
    private final String modId;

    public ItemRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize AddonItems on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.createItems(modId));
        ITEMS.put(modId, new ArrayList<>());
    }

    static DeferredRegister.Items getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    public List<ItemDefinition<?>> getItems() {
        return getItems(this.modId);
    }

    public static List<ItemDefinition<?>> getItems(String modId) {
        return Collections.unmodifiableList(ITEMS.get(modId));
    }

    protected static <T extends Item> ItemDefinition<T> item(
            String modId, String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new ItemDefinition<>(englishName, getDR(modId).registerItem(id, factory));
        ITEMS.get(modId).add(definition);
        return definition;
    }

    protected static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String modId, String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(modId, englishName, id, p -> new PartItem<>(p, partClass, factory));
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
