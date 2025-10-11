package net.pedroksl.ae2addonlib.registry;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

public class AddonMenus {

    static AddonMenus INSTANCE;
    private final DeferredRegister<MenuType<?>> DR;
    private final String modId;

    public AddonMenus(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonMenus on Client Dist.");
        }

        INSTANCE = this;

        this.modId = modId;
        DR = DeferredRegister.create(Registries.MENU, modId);
    }

    private static AddonMenus getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Menu Registration is not initialized.");
        }
        return INSTANCE;
    }

    static DeferredRegister<MenuType<?>> getDR() {
        return getInstance().DR;
    }

    protected static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return getDR().register(id, () -> MenuTypeBuilder.create(factory, host)
                .build(ResourceLocation.fromNamespaceAndPath(INSTANCE.modId, id)));
    }

    protected static <T extends AEBaseMenu> Supplier<MenuType<T>> create(String id, Supplier<MenuType<T>> supplier) {
        return getDR().register(id, supplier);
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
