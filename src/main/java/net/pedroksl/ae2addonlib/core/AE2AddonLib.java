package net.pedroksl.ae2addonlib.core;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.pedroksl.ae2addonlib.core.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.registry.helpers.LibComponents;
import net.pedroksl.ae2addonlib.registry.helpers.LibMenus;
import net.pedroksl.ae2addonlib.util.LibAddons;

/**
 * Main lib class.
 */
public class AE2AddonLib {
    /**
     * The MOD_ID of this lib
     */
    public static final String MOD_ID = "ae2addonlib";
    /**
     * A static instance of this lib's main class.
     */
    public static AE2AddonLib INSTANCE;

    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The lib's constructor.
     * @param eventBus The event bus.
     * @param modContainer The mod container.
     */
    public AE2AddonLib(IEventBus eventBus, ModContainer modContainer) {
        assert INSTANCE == null;
        INSTANCE = this;

        LibMenus.INSTANCE.register(eventBus);
        LibComponents.INSTANCE.register(eventBus);

        eventBus.addListener(LibNetworkHandler.INSTANCE::register);
        eventBus.addListener(AE2AddonLib::imc);
    }

    /**
     * Helper method to easily create identifiers using this lib's namespace.
     * @param path The path of the desired identifier.
     * @return A constructed {@link Identifier} in this lib's namespace.
     */
    public static Identifier makeId(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Send this lib's {@link InterModComms}.
     * @param event The {@link InterModEnqueueEvent}.
     */
    public static void imc(InterModEnqueueEvent event) {
        if (LibAddons.DARKMODEEVERYWHERE.isLoaded()) {
            InterModComms.sendTo(
                    LibAddons.DARKMODEEVERYWHERE.getModId(),
                    "dme-shaderblacklist",
                    () -> "net.pedroksl.ae2addonlib.client.");
        }
    }
}
