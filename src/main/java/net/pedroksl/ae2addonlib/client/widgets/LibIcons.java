package net.pedroksl.ae2addonlib.client.widgets;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.pedroksl.ae2addonlib.AE2AddonLib;

/**
 * The lib's icons. Used in the libs screens.
 */
public enum LibIcons implements IBlitterIcon {
    /**
     * The icon for the direction output toolbar side button.
     */
    DIRECTION_OUTPUT(0, 0),

    /**
     * The icon for a small icon with an x.
     */
    CLEAR_SMALL(0, 16),

    /**
     * The icon for the background of a toolbar button.
     */
    TOOLBAR_BUTTON_BACKGROUND(176, 128, 18, 18),

    /**
     * The icon for an enabled toolbar button.
     */
    TOOLBAR_BUTTON_ENABLED(194, 128, 18, 18);

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private static final ResourceLocation TEXTURE = AE2AddonLib.makeId("textures/guis/states.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    LibIcons(int x, int y) {
        this(x, y, 16, 16);
    }

    LibIcons(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public Size getTextureSize() {
        return new Size(TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public Rect2i getRect() {
        return new Rect2i(x, y, width, height);
    }
}
