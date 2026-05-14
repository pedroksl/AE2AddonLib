package net.pedroksl.ae2addonlib.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;

import appeng.datagen.providers.models.AE2ModelProvider;

/**
 * Datagen class used to generate the lib's language files.
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = AE2AddonLib.MOD_ID)
public class DataGen {

    /**
     * Event handler used to generate language files.
     * @param event The {@link GatherDataEvent}.
     */
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        var gen = event.getGenerator();

        var out = gen.getPackOutput();
        var pack = gen.getVanillaPack(true);

        var languageProvider = new LibLanguageProvider(out);

        pack.addProvider(AE2ModelProvider.create(AE2AddonLib.MOD_ID, TestModelProvider::new));

        pack.addProvider(packOutput -> languageProvider);
    }
}
