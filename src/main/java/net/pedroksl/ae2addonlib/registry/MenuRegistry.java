package net.pedroksl.ae2addonlib.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

/**
 * <p>Class responsible for the registering of menus.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 * You will still need to link the Screens to these menus in a client event using {@link appeng.init.client.InitScreens}.
 */
public abstract class MenuRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<MenuType<?>>> DRMap = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register.
     * @param modId The MOD_ID of the mod creating this instance.
     */
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

    /**
     * Helper method used to create {@link Supplier} of {@link MenuType} without manually registering the type.
     * @param modId The MOD_ID of the mod creating this instance.
     * @param id String id of the menu.
     * @param factory A constructor of the menu to be registered that conforms to the {@link appeng.menu.implementations.MenuTypeBuilder.MenuFactory} interface.
     * @param host The class of the menu host.
     * @param <M> Menu class that extends {@link AEBaseMenu}.
     * @param <H> Menu host class.
     * @return A supplier of the registered menu type.
     */
    protected static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String modId, String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return getDR(modId)
                .register(id, () -> MenuTypeBuilder.create(factory, host).build(modId + "_" + id));
    }

    /**
     * Helper method used to create {@link Supplier} of {@link MenuType} without manually registering the type.
     * @param modId The MOD_ID of the mod creating this instance.
     * @param id String id of the menu.
     * @param supplier A supplier for an already registered menu type.
     * @param <T> Menu class that extends {@link AEBaseMenu}.
     * @return A supplier of the registered menu type.
     */
    protected static <T extends AEBaseMenu> Supplier<MenuType<T>> create(
            String modId, String id, Supplier<MenuType<T>> supplier) {
        return getDR(modId).register(id, supplier);
    }

    /**
     * Used to finalize the menu registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
