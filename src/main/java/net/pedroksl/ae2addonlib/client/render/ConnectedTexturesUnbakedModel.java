package net.pedroksl.ae2addonlib.client.render;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

public class ConnectedTexturesUnbakedModel implements UnbakedModel {
    private final ConnectedTexturesBaseBakedModel model;

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
            ModelBaker loader, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        return model;
    }
}
