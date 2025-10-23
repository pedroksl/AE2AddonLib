package net.pedroksl.ae2addonlib;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.client.screens.SetAmountScreen;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
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
     */
    public AE2AddonLib() {
        assert INSTANCE == null;
        INSTANCE = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        LibMenus.INSTANCE.register(eventBus);
        eventBus.addListener(AE2AddonLib::commonSetup);

        // eventBus.addListener(LibNetworkHandler.INSTANCE::register);
        eventBus.addListener(AE2AddonLib::imc);
    }

    private static void commonSetup(final FMLCommonSetupEvent e) {
        LibNetworkHandler.INSTANCE.init();
    }

    /**
     * Helper method to easily create resource locations using this lib's namespace.
     * @param path The path of the desired resource location.
     * @return A constructed {@link ResourceLocation} in this lib's namespace.
     */
    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /**
     * Send this lib's {@link InterModComms}.
     * @param event The {@link InterModEnqueueEvent}.
     */
    private static void imc(InterModEnqueueEvent event) {
        if (LibAddons.DARKMODEEVERYWHERE.isLoaded()) {
            InterModComms.sendTo(
                    LibAddons.DARKMODEEVERYWHERE.getModId(),
                    "dme-shaderblacklist",
                    () -> "net.pedroksl.ae2addonlib.client.");
        }
    }

    @Mod.EventBusSubscriber(modid = AE2AddonLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class AE2AddonClient {
        @SubscribeEvent
        static void onRegisterMenuScreenEvent(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                InitScreens.register(
                        LibMenus.OUTPUT_DIRECTION.get(), OutputDirectionScreen::new, "/screens/output_direction.json");
                InitScreens.register(LibMenus.SET_AMOUNT.get(), SetAmountScreen::new, "/screens/set_amount.json");
            });
        }
    }
}
