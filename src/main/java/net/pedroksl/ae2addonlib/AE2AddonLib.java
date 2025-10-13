package net.pedroksl.ae2addonlib;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.registry.helpers.AddonMenus;

import appeng.init.client.InitScreens;

@Mod(AE2AddonLib.MOD_ID)
public class AE2AddonLib {
    public static final String MOD_ID = "ae2addonlib";
    public static AE2AddonLib INSTANCE;

    public AE2AddonLib(IEventBus eventBus, ModContainer modContainer) {
        assert INSTANCE == null;
        INSTANCE = this;

        AddonMenus.INSTANCE.register(eventBus);
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SuppressWarnings("deprecation")
    @EventBusSubscriber(modid = AE2AddonLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    static class AE2AddonClient {
        @SubscribeEvent
        static void onRegisterMenuScreenEvent(RegisterMenuScreensEvent event) {
            InitScreens.register(
                    event,
                    AddonMenus.OUTPUT_DIRECTION.get(),
                    OutputDirectionScreen::new,
                    "/screens/output_direction.json");
        }
    }
}
