package net.pedroksl.ae2addonlib.client.render;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

/**
 * Unbaked model that uses a {@link ConnectedTexturesBaseBakedModel} as its texture provider.
 */
public class ConnectedTexturesUnbakedModel implements UnbakedModel {
    private final ConnectedTexturesBaseBakedModel model;

    /**
     * Constructor of the unbaked model
     * @param model The {@link ConnectedTexturesBaseBakedModel} that will provide textures for this unbaked model.
     */
    public ConnectedTexturesUnbakedModel(ConnectedTexturesBaseBakedModel model) {
        this.model = model;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {}

    @Override
    public @Nullable BakedModel bake(
            ModelBaker modelBaker,
            Function<Material, TextureAtlasSprite> function,
            ModelState modelState,
            ResourceLocation resourceLocation) {
        return model;
    }
}
