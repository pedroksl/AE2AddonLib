package net.pedroksl.ae2addonlib.util;

import java.util.function.Consumer;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

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
     * Helper method to create a recipe that is conditional to a certain mod being loaded.
     * Used for most processing recipes with a custom build/save step.
     * @param output The recipe output of the conditional recipe.
     * @param recipe The recipe builder for the recipe.
     * @param id The resource location of the recipe.
     */
    default void conditionalRecipe(
            Consumer<FinishedRecipe> output, Consumer<Consumer<FinishedRecipe>> recipe, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(getModId()))
                .addRecipe(recipe)
                .build(output, id);
    }

    /**
     * Helper method to create a recipe that is conditional to a certain mod being loaded.
     * Used for regular crafting recipes or processing recipes whose recipe builder extends {@link RecipeBuilder}.
     * @param output The recipe output of the conditional recipe.
     * @param recipe The recipe builder for the recipe.
     * @param id The resource location of the recipe.
     */
    default void conditionalRecipe(Consumer<FinishedRecipe> output, RecipeBuilder recipe, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(getModId()))
                .addRecipe(recipe::save)
                .build(output, id);
    }

    /**
     * Helper method to create a recipe that is conditional to a certain mod not being loaded
     * @param output The recipe output of the conditional recipe.
     * @param recipe The recipe builder for the recipe.
     * @param id The resource location of the recipe.
     */
    default void notConditionalRecipe(Consumer<FinishedRecipe> output, RecipeBuilder recipe, ResourceLocation id) {
        ConditionalRecipe.builder()
                .addCondition(new NotCondition(new ModLoadedCondition(getModId())))
                .addRecipe(recipe::save)
                .build(output, id);
    }
}
