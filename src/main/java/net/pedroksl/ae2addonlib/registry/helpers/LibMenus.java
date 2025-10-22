package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Supplier;

import net.minecraft.world.inventory.MenuType;
import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;
import net.pedroksl.ae2addonlib.gui.OutputDirectionMenu;
import net.pedroksl.ae2addonlib.gui.SetAmountMenu;
import net.pedroksl.ae2addonlib.registry.MenuRegistry;

import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

/**
 * <p>Lib provided menu set.</p>
 * This class contains some useful general menus. Additional menus will likely be added in the future.
 */
public class LibMenus extends MenuRegistry {

    /**
     * The class' singleton instance, used to request registration.
     */
    public static final LibMenus INSTANCE = new LibMenus();

    LibMenus() {
        super(AE2AddonLib.MOD_ID);
    }

    /**
     * A menu that manages an {@link net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen}.
     * Can be attached to block entities that implement the {@link IDirectionalOutputHost} interface.
     */
    public static final Supplier<MenuType<OutputDirectionMenu>> OUTPUT_DIRECTION =
            create("output_direction", OutputDirectionMenu::new, IDirectionalOutputHost.class);

    /**
     * A menu that opens a {@link net.pedroksl.ae2addonlib.client.screens.SetAmountScreen} to request a value from the player.
     * It can handle item vs fluid input and will call the provided function when done.
     */
    public static final Supplier<MenuType<SetAmountMenu>> SET_AMOUNT =
            create("set_amount", SetAmountMenu::new, ISubMenuHost.class);

    private static <M extends AEBaseMenu, H> Supplier<MenuType<M>> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        return create(AE2AddonLib.MOD_ID, id, factory, host);
    }
}
