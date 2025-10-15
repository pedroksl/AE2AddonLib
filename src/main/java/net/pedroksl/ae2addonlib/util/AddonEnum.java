package net.pedroksl.ae2addonlib.util;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

public interface AddonEnum {

    String getModId();

    String getModName();

    default boolean isLoaded() {
        return ModList.get() != null
                ? ModList.get().isLoaded(getModId())
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(getModId()::equals);
    }

    default RecipeOutput conditionalRecipe(RecipeOutput output) {
        return output.withConditions(new ModLoadedCondition(getModId()));
    }

    default RecipeOutput notConditionalRecipe(RecipeOutput output) {
        return output.withConditions(new NotCondition(new ModLoadedCondition(getModId())));
    }
}
