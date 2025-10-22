package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.ae2addonlib.registry.helpers.MaterialDefinition;

/**
 * <p>Class responsible for the registering of materials.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class MaterialRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<ArmorMaterial>> DRMap = new HashMap<>();
    private static final Map<String, List<MaterialDefinition<?>>> MATERIALS = new HashMap();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and MATERIALS map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public MaterialRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize MaterialRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, modId));
        MATERIALS.put(modId, new ArrayList<>());
    }

    static DeferredRegister<ArmorMaterial> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    /**
     * Non-static version of {@link #getMaterials(String)}.
     * @return A list containing all registered {@link MaterialDefinition}s.
     */
    public List<MaterialDefinition<?>> getMaterials() {
        return getMaterials(this.modId);
    }

    /**
     * Helper method to create a collection of all registered materials.
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link MaterialDefinition}s.
     */
    public static List<MaterialDefinition<?>> getMaterials(String modId) {
        return Collections.unmodifiableList(MATERIALS.getOrDefault(modId, new ArrayList<>()));
    }

    /**
     * Material registration method.
     * @param modId The MOD_ID of the requesting mod.
     * @param englishName Human-readable string to name the block. Can be used in a language provider to generate translations alongside {@link #getMaterials()}.
     * @param id The id of the registered block.
     * @param material The material supplier
     * @return The {@link MaterialDefinition} containing all relevant information for this material.
     */
    protected static MaterialDefinition<ArmorMaterial> material(
            String modId, String englishName, String id, Supplier<ArmorMaterial> material) {
        var definition = new MaterialDefinition<>(englishName, getDR(modId).register(id, material));
        MATERIALS.get(modId).add(definition);
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
