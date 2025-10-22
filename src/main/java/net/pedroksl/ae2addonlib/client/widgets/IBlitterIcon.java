package net.pedroksl.ae2addonlib.client.widgets;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.style.Blitter;

/**
 * Wraps a class containing resource locations for use in several widgets
 */
public interface IBlitterIcon {

    /**
     * Gets the icon texture.
     * @return The icon texture's {@link ResourceLocation}
     */
    ResourceLocation getTexture();

    /**
     * Gets the texture size.
     * @return The {@link Size} of the texture.
     */
    Size getTextureSize();

    /**
     * Gets the texture rect.
     * @return The {@link Rect2i} of the texture.
     */
    Rect2i getRect();

    /**
     * Gets the texture blitter. The default implementation should be enough for most cases.
     * @return The texture's {@link Blitter}.
     */
    default Blitter getBlitter() {
        var size = getTextureSize();
        return Blitter.texture(getTexture(), size.width, size.height).src(getRect());
    }

    /**
     * Container for a size.
     * @param width The size's width.
     * @param height The size's height.
     */
    record Size(int width, int height) {}
}
