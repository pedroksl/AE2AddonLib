package net.pedroksl.ae2addonlib.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.core.definitions.BlockDefinition;

/**
 * <p>Class responsible for the registering of components.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class ComponentRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<DataComponentType<?>>> DRMap = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public ComponentRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize ComponentRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, modId));
    }

    static DeferredRegister<DataComponentType<?>> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    /**
     * Non-static version of {@link #getEntries(String)}.
     * @return A list containing all registered {@link DeferredHolder} of {@link DataComponentType}.
     */
    public Collection<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<?>>> getEntries() {
        return getEntries(this.modId);
    }

    /**
     * Helper method to create a collection of all registered components
     * @param modId The MOD_ID of the requesting mod.
     * @return A list containing all registered {@link DeferredHolder} of {@link DataComponentType}.
     */
    public static Collection<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<?>>> getEntries(
            String modId) {
        try {
            return getDR(modId).getEntries();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Registration method for components
     * @param modId The MOD_ID of the requesting mod.
     * @param id The id of the registered block.
     * @param customizer Builder method for this component.
     * @param <T> The generic held by the {@link DataComponentType}
     * @return The {@link BlockDefinition} containing all relevant information for this block.
     */
    protected static <T> DataComponentType<T> register(
            String modId, String id, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        getDR(modId).register(id, () -> componentType);
        return componentType;
    }

    /**
     * Used to finalize the component registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
