package net.pedroksl.ae2addonlib.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.client.screens.SetAmountScreen;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.helpers.LibMenus;
import net.pedroksl.ae2addonlib.util.ColoredItemTintSource;

import appeng.client.InitScreens;

@Mod(value = AE2AddonLib.MOD_ID, dist = Dist.CLIENT)
public class AE2AddonLibClient extends AE2AddonLib {

    private static AE2AddonLibClient INSTANCE;

    public AE2AddonLibClient(IEventBus modEventBus, ModContainer container) {
        super(modEventBus, container);

        modEventBus.addListener(this::onRegisterMenuScreenEvent);

        modEventBus.addListener(new LibClientNetworkHandler()::registerPackets);
    }

    private void onRegisterMenuScreenEvent(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event, LibMenus.OUTPUT_DIRECTION.get(), OutputDirectionScreen::new, "/screens/output_direction.json");
        InitScreens.register(event, LibMenus.SET_AMOUNT.get(), SetAmountScreen::new, "/screens/set_amount.json");
    }

    private static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ColoredItemTintSource.ID, ColoredItemTintSource.MAP_CODEC);
    }
}
