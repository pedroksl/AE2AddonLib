package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Function;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;

/**
 * <p>Class responsible for the registering of items.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class ItemRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister.Items> DRMap = new HashMap<>();
    private static final Map<String, List<ItemDefinition<?>>> ITEMS = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and ITEMS map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public ItemRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.getDist().isClient()) {
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

    /**
     * Non-static version of {@link #getItems(String)}.
     * @return A list containing all registered {@link ItemDefinition}s.
     */
    public List<ItemDefinition<?>> getItems() {
        return getItems(this.modId);
    }

    /**
     * Helper method to create a collection of all registered items.
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link ItemDefinition}s.
     */
    public static List<ItemDefinition<?>> getItems(String modId) {
        return Collections.unmodifiableList(ITEMS.getOrDefault(modId, new ArrayList<>()));
    }

    /**
     * Registers an item.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getItems()}.
     * @param id The id of the registered item.
     * @param factory The item construction factory.
     * @param <T> The item class that extends {@link Item}.
     * @return The {@link ItemDefinition} containing all relevant information for this block.
     */
    protected static <T extends Item> ItemDefinition<T> item(
            String englishName, Identifier id, Function<Item.Properties, T> factory) {
        var modId = id.getNamespace();
        var definition = new ItemDefinition<>(englishName, getDR(modId).registerItem(id.getPath(), factory));
        ITEMS.get(modId).add(definition);
        return definition;
    }

    /**
     * Registers a part item. This method also registers the part models.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getItems()}.
     * @param id The id of the registered item.
     * @param partClass The class of the registered part.
     * @param factory The item construction factory.
     * @param <T> The part class that extends {@link IPart}.
     * @return The {@link ItemDefinition} containing all relevant information for this block.
     */
    protected static <T extends IPart> ItemDefinition<PartItem<T>> part(
            String englishName, Identifier id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        return item(englishName, id, p -> new PartItem<>(p, partClass, factory));
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
