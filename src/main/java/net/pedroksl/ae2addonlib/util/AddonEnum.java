package net.pedroksl.ae2addonlib.util;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

/**
 * Interface that adds helper methods for an enumeration of integration mods.
 */
public interface AddonEnum {

    /**
     * Reads the entry's mod id.
     * @return The entry's mod id
     */
    String getModId();

    /**
     * Reads the entry's mod name.
     * @return The entry's mod name.
     */
    String getModName();

    /**
     * Helper method to check if an integrated mod is loaded
     * @return If the requested mod is loaded or not.
     */
    default boolean isLoaded() {
        return ModList.get() != null
                ? ModList.get().isLoaded(getModId())
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(getModId()::equals);
    }

    /**
     * Helper method to create a recipe that is conditional to a certain mod being loaded
     * @param output The recipe output of the conditional recipe.
     * @return The recipe output for further processing.
     */
    default RecipeOutput conditionalRecipe(RecipeOutput output) {
        return output.withConditions(new ModLoadedCondition(getModId()));
    }

    /**
     * Helper method to create a recipe that is conditional to a certain mod not being loaded
     * @param output The recipe output of the conditional recipe.
     * @return The recipe output for further processing.
     */
    default RecipeOutput notConditionalRecipe(RecipeOutput output) {
        return output.withConditions(new NotCondition(new ModLoadedCondition(getModId())));
    }
}
