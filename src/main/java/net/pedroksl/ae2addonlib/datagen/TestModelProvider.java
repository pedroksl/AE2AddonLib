package net.pedroksl.ae2addonlib.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

import appeng.datagen.providers.models.PartModelOutput;

public class TestModelProvider extends AE2AddonModelProvider {

    /**
     * Constructs the provider's instance.
     *
     * @param blockModels The Block Model Generators.
     * @param itemModels  The Item Model Generators.
     * @param partModels  The Part Model Output.
     */
    public TestModelProvider(
            BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
        super(blockModels, itemModels, partModels);
    }

    @Override
    protected void register() {}
}
