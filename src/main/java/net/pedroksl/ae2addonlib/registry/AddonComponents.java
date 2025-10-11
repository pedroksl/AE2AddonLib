package net.pedroksl.ae2addonlib.registry;

import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonComponents {

    static AddonComponents INSTANCE;
    private final DeferredRegister<DataComponentType<?>> DR;

    public AddonComponents(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonComponents on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, modId);
    }

    private static AddonComponents getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Component Registration is not initialized.");
        }

        return INSTANCE;
    }

    static DeferredRegister<DataComponentType<?>> getDR() {
        return getInstance().DR;
    }

    public static Collection<DeferredHolder<DataComponentType<?>, ? extends DataComponentType<?>>> getEntries() {
        return getDR().getEntries();
    }

    protected static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        getDR().register(name, () -> componentType);
        return componentType;
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
