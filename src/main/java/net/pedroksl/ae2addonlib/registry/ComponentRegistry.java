package net.pedroksl.ae2addonlib.registry;

import java.util.Collection;
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

public class ComponentRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<DataComponentType<?>>> DRMap = new HashMap<>();
    private final String modId;

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

    public Collection<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<?>>> getEntries() {
        return getEntries(this.modId);
    }

    public static Collection<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<?>>> getEntries(
            String modId) {
        return getDR(modId).getEntries();
    }

    protected static <T> DataComponentType<T> register(
            String modId, String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        getDR(modId).register(name, () -> componentType);
        return componentType;
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
