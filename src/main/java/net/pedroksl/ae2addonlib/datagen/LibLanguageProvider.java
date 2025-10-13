package net.pedroksl.ae2addonlib.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.LibText;

public class LibLanguageProvider extends LanguageProvider {
    public LibLanguageProvider(PackOutput output) {
        super(output, AE2AddonLib.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (var translation : LibText.values()) {
            add(translation.getTranslationKey(), translation.getEnglishText());
        }
    }
}
