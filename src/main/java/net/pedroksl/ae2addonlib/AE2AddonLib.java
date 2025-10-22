package net.pedroksl.ae2addonlib;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.client.screens.SetAmountScreen;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.registry.helpers.LibComponents;
import net.pedroksl.ae2addonlib.registry.helpers.LibMenus;
import net.pedroksl.ae2addonlib.util.LibAddons;

import appeng.init.client.InitScreens;

/**
 * Main lib class.
 */
@Mod(AE2AddonLib.MOD_ID)
public class AE2AddonLib {
    /**
     * The MOD_ID of this lib
     */
    public static final String MOD_ID = "ae2addonlib";
    /**
     * A static instance of this lib's main class.
     */
    public static AE2AddonLib INSTANCE;

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
     * Helper method to easily create resource locations using this lib's namespace.
     * @param path The path of the desired resource location.
     * @return A constructed {@link ResourceLocation} in this lib's namespace.
     */
    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
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

    @EventBusSubscriber(modid = AE2AddonLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class AE2AddonClient {
        @SubscribeEvent
        static void onRegisterMenuScreenEvent(RegisterMenuScreensEvent event) {
            InitScreens.register(
                    event,
                    LibMenus.OUTPUT_DIRECTION.get(),
                    OutputDirectionScreen::new,
                    "/screens/output_direction.json");
            InitScreens.register(event, LibMenus.SET_AMOUNT.get(), SetAmountScreen::new, "/screens/set_amount.json");
        }
    }
}
