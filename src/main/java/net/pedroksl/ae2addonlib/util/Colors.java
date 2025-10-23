package net.pedroksl.ae2addonlib.util;

import java.awt.*;

import org.jetbrains.annotations.NotNull;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

/**
 * Helper class used to create/convert colors to/from different color spaces.
 */
@SuppressWarnings("unused")
public final class Colors {

    /**
     * A light gray color with a blue hue.
     */
    public static final Colors LIGHT_GRAY_BLUE = Colors.ofArgb(0xFFADB0C4);

    /**
     * A dark gray color with a blue hue.
     */
    public static final Colors DARK_GRAY_BLUE = Colors.ofArgb(0xFF413F54);

    /**
     * A light purple color.
     */
    public static final Colors LIGHT_PURPLE = Colors.ofArgb(0x787d53c1);

    /**
     * A purple color.
     */
    public static final Colors PURPLE = Colors.ofArgb(0xFF7110a5);

    /**
     * The color white.
     */
    public static final Colors WHITE = new Colors(255, 255, 255);

    /**
     * The color light gray.
     */
    public static final Colors LIGHT_GRAY = new Colors(192, 192, 192);

    /**
     * The color gray.
     */
    public static final Colors GRAY = new Colors(128, 128, 128);

    /**
     * The color dark gray.
     */
    public static final Colors DARK_GRAY = new Colors(64, 64, 64);

    /**
     * The color black.
     */
    public static final Colors BLACK = new Colors(0, 0, 0);

    /**
     * The color red.
     */
    public static final Colors RED = new Colors(255, 0, 0);

    /**
     * The color pink.
     */
    public static final Colors PINK = new Colors(255, 175, 175);

    /**
     * The color orange.
     */
    public static final Colors ORANGE = new Colors(255, 200, 0);

    /**
     * The color yellow.
     */
    public static final Colors YELLOW = new Colors(255, 255, 0);

    /**
     * The color green.
     */
    public static final Colors GREEN = new Colors(0, 255, 0);

    /**
     * The color magenta.
     */
    public static final Colors MAGENTA = new Colors(255, 0, 255);

    /**
     * The color cyan.
     */
    public static final Colors CYAN = new Colors(0, 255, 255);

    /**
     * The color blue.
     */
    public static final Colors BLUE = new Colors(0, 0, 255);

    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    Colors(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    Colors(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Creates a new ARGB color from an int value.
     * @param color The int value of the color.
     * @return The constructed Colors instance.
     */
    public static Colors ofArgb(int color) {
        var alpha = color >> 24 & 0xFF;
        var red = color >> 16 & 0xFF;
        var green = color >> 8 & 0xFF;
        var blue = color & 0xFF;

        return new Colors(red, green, blue, alpha);
    }

    /**
     * Creates a new RGB color from an int value.
     * @param color The int value of the color.
     * @return The constructed Colors instance.
     */
    public static Colors ofRgb(int color) {
        var red = color >> 16 & 0xFF;
        var green = color >> 8 & 0xFF;
        var blue = color & 0xFF;

        return new Colors(red, green, blue);
    }

    /**
     * Creates a new color using values from the HSV color space.
     * All values are expected to be in the 0 - 1f range.
     * Assumes maximum alpha.
     * @param hue The color hue.
     * @param saturation The color saturation.
     * @param value The color value.
     * @return The constructed Colors instance.
     */
    public static Colors ofHsv(float hue, float saturation, float value) {
        return ofRgb(Mth.hsvToRgb(hue - 0.5e-7f, saturation, value));
    }

    /**
     * Creates a new color using values from the HSV color space.
     * All values are expected to be in the 0 - 1f range.
     * @param hue The color hue.
     * @param saturation The color saturation.
     * @param value The color value.
     * @param alpha The color alpha.
     * @return The constructed Colors instance.
     */
    public static @NotNull Colors ofHsv(float hue, float saturation, float value, float alpha) {
        int color = Mth.hsvToRgb(hue - 0.5e-7f, saturation, value);
        return ofArgb(color | (int) (alpha * 255) << 24);
    }

    /**
     * Getter for the red value.
     * @return The red value of this color.
     */
    public float r() {
        return this.red / 255f;
    }

    /**
     * Getter for the green value.
     * @return The green value of this color.
     */
    public float g() {
        return this.green / 255f;
    }

    /**
     * Getter for the blue value.
     * @return The blue value of this color.
     */
    public float b() {
        return this.blue / 255f;
    }

    /**
     * Getter for the alpha value.
     * @return The alpha value of this color.
     */
    public float a() {
        return this.alpha / 255f;
    }

    /**
     * Converts the color to ARGB color space.
     * @return The color in ARGB color space as an int value.
     */
    public int argb() {
        return FastColor.ARGB32.color(this.alpha, this.red, this.green, this.blue);
    }

    /**
     * Converts the color to ARGB color space with a given alpha.
     * @param alpha The alpha value to be used in the conversion
     * @return The color in ARGB color space as an int value.
     */
    public int argb(int alpha) {
        return FastColor.ARGB32.color(alpha, this.red, this.green, this.blue);
    }

    /**
     * Converts the color to RBG color space.
     * @return The color in RGB color space.
     */
    public int rgb() {
        return this.red << 16 | this.green << 8 | this.blue;
    }

    /**
     * Converts the color to HSV color space.
     * @return The color in HSV color space.
     */
    public HSV hsv() {
        float[] vals = new float[3];
        Color.RGBtoHSB(this.red, this.green, this.blue, vals);
        return new HSV(vals[0], vals[1], vals[2]);
    }

    /**
     * A container for a color in HSV color space.
     * @param hue The color hue
     * @param saturation The color saturation
     * @param value The color value
     */
    public record HSV(float hue, float saturation, float value) {}
}
