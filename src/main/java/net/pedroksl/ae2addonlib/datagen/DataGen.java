package net.pedroksl.ae2addonlib.datagen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.pedroksl.ae2addonlib.AE2AddonLib;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = AE2AddonLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DataGen {

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
