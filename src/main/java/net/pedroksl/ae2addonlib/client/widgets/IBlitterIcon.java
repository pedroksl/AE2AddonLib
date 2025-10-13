package net.pedroksl.ae2addonlib.client.widgets;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;

public interface IBlitterIcon {

    ResourceLocation getTexture();

    Size getTextureSize();

    Rect2i getRect();

    default Blitter getBlitter() {
        var size = getTextureSize();
        return Blitter.texture(getTexture(), size.width, size.height).src(getRect());
    }

    record Size(int width, int height) {}
}
