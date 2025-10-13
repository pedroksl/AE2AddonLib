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

public class MaterialRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<ArmorMaterial>> DRMap = new HashMap<>();
    private static final Map<String, List<MaterialDefinition<?>>> MATERIALS = new HashMap();
    private final String modId;

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

    public List<MaterialDefinition<?>> getMaterials() {
        return getMaterials(this.modId);
    }

    public static List<MaterialDefinition<?>> getMaterials(String modId) {
        return Collections.unmodifiableList(MATERIALS.get(modId));
    }

    protected static MaterialDefinition<ArmorMaterial> material(
            String modId, String englishName, String id, Supplier<ArmorMaterial> material) {
        var definition = new MaterialDefinition<>(englishName, getDR(modId).register(id, material));
        MATERIALS.get(modId).add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
