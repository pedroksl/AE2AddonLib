package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Container record for material registration.
 * @param englishName Human-readable english name of the material.
 * @param material The {@link DeferredHolder} of the material.
 * @param <T> The class that extends {@link ArmorMaterial}.
 */
public record MaterialDefinition<T extends ArmorMaterial>(String englishName, DeferredHolder<ArmorMaterial, T> material)
        implements Supplier<T> {

    /**
     * Getter for the resource location of the material definition.
     * @return The resource location of the material.
     */
    public ResourceLocation id() {
        return this.material.getId();
    }

    /**
     * Getter for the registered material inside the holder.
     * @return The registered material.
     */
    public T get() {
        return this.material.get();
    }

    /**
     * Getter for the registered material inside the holder.
     * @return The registered material.
     */
    public T asItem() {
        return this.material.get();
    }
}
