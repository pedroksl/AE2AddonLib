package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.ae2addonlib.registry.helpers.MaterialDefinition;

public class AddonMaterials {

    static AddonMaterials INSTANCE;
    private final DeferredRegister<ArmorMaterial> DR;

    public AddonMaterials(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonMaterials on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, modId);
    }

    private static AddonMaterials getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Material Registration is not initialized.");
        }
        return INSTANCE;
    }

    static DeferredRegister<ArmorMaterial> getDR() {
        return getInstance().DR;
    }

    private static final List<MaterialDefinition<?>> MATERIALS = new ArrayList<>();

    public static List<MaterialDefinition<?>> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    protected static MaterialDefinition<ArmorMaterial> material(
            String englishName, String id, Supplier<ArmorMaterial> material) {
        var definition = new MaterialDefinition<>(englishName, getDR().register(id, material));
        MATERIALS.add(definition);
        return definition;
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
