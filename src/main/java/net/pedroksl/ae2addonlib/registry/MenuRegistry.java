package net.pedroksl.ae2addonlib.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

public abstract class MenuRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<MenuType<?>>> DRMap = new HashMap<>();
    private final String modId;

    public MenuRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize MenuRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.create(Registries.MENU, modId));
    }

    static DeferredRegister<MenuType<?>> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    protected static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String modId, String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return getDR(modId).register(id, () -> MenuTypeBuilder.create(factory, host)
                .build(ResourceLocation.fromNamespaceAndPath(modId, id)));
    }

    protected static <T extends AEBaseMenu> Supplier<MenuType<T>> create(
            String modId, String id, Supplier<MenuType<T>> supplier) {
        return getDR(modId).register(id, supplier);
    }

    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
