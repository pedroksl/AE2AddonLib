package net.pedroksl.ae2addonlib.datagen;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pedroksl.ae2addonlib.AE2AddonLib;

/**
 * Datagen class used to generate the lib's language files.
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AE2AddonLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    /**
     * Event handler used to generate language files.
     * @param event The {@link GatherDataEvent}.
     */
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var fileHelper = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();
        var languageProvider = new LibLanguageProvider(out);

        gen.addProvider(event.includeClient(), languageProvider);
    }
}
