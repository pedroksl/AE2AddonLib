package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Function;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;

/**
 * <p>Class responsible for the registering of items.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class ItemRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<Item>> DRMap = new HashMap<>();
    private static final Map<String, List<LibItemDefinition<?>>> ITEMS = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and ITEMS map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public ItemRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize AddonItems on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.create(ForgeRegistries.ITEMS, modId));
        ITEMS.put(modId, new ArrayList<>());
    }

    static DeferredRegister<Item> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    /**
     * Non-static version of {@link #getItems(String)}.
     * @return A list containing all registered {@link LibItemDefinition}s.
     */
    public List<LibItemDefinition<?>> getItems() {
        return getItems(this.modId);
    }

    /**
     * Helper method to create a collection of all registered items.
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link LibItemDefinition}s.
     */
    public static List<LibItemDefinition<?>> getItems(String modId) {
        return Collections.unmodifiableList(ITEMS.getOrDefault(modId, new ArrayList<>()));
    }

    /**
     * Registers an item.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getItems()}.
     * @param id The id of the registered item.
     * @param factory The item construction factory.
     * @param <T> The item class that extends {@link Item}.
     * @return The {@link LibItemDefinition} containing all relevant information for this block.
     */
    protected static <T extends Item> LibItemDefinition<T> item(
            String modId, String englishName, String id, Function<Item.Properties, T> factory) {
        var definition = new LibItemDefinition<>(
                englishName, getDR(modId).register(id, () -> factory.apply(new Item.Properties())));
        ITEMS.get(modId).add(definition);
        return definition;
    }

    /**
     * Registers a part item. This method also registers the part models. I will look for {@link net.minecraft.resources.ResourceLocation}s
     * marked with {@link appeng.items.parts.PartModels} annotation.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getItems()}.
     * @param id The id of the registered item.
     * @param partClass The class of the registered part.
     * @param factory The item construction factory.
     * @param <T> The part class that extends {@link IPart}.
     * @return The {@link LibItemDefinition} containing all relevant information for this block.
     */
    protected static <T extends IPart> LibItemDefinition<PartItem<T>> part(
            String modId, String englishName, String id, Class<T> partClass, Function<IPartItem<T>, T> factory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(modId, englishName, id, p -> new PartItem<>(p, partClass, factory));
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
