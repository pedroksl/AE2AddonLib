package net.pedroksl.ae2addonlib.client.render;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

public class ConnectedTexturesUnbakedModel implements UnbakedModel {
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of();
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {}

    @Override
    public @Nullable BakedModel bake(
            ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState) {
        return null;
    }
}
