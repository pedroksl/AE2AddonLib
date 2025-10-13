package net.pedroksl.ae2addonlib.util;

import java.awt.*;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class Colors {

    public static final Colors WHITE = Colors.ofArgb(0xFFFFFFFF);
    public static final Colors LIGHT_GRAY = Colors.ofArgb(0xFFADB0C4);
    public static final Colors DARK_GRAY_BLUE = Colors.ofArgb(0xFF413F54);
    public static final Colors LIGHT_PURPLE = Colors.ofArgb(0x787d53c1);
    public static final Colors PURPLE = Colors.ofArgb(0xFF7110a5);
    public static final Colors DARK_GRAY = Colors.ofArgb(0X8B8B8B);

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

    public static Colors ofArgb(int color) {
        var alpha = color >> 24 & 0xFF;
        var red = color >> 16 & 0xFF;
        var green = color >> 8 & 0xFF;
        var blue = color & 0xFF;

        return new Colors(red, green, blue, alpha);
    }

    public static Colors ofRgb(int color) {
        var red = color >> 16 & 0xFF;
        var green = color >> 8 & 0xFF;
        var blue = color & 0xFF;

        return new Colors(red, green, blue);
    }

    public static Colors ofHsv(float hue, float saturation, float value) {
        return ofArgb(Mth.hsvToArgb(hue - 0.5e-7f, saturation, value, 255));
    }

    public static Colors ofHsv(float hue, float saturation, float value, float alpha) {
        return ofArgb(Mth.hsvToArgb(hue - 0.5e-7f, saturation, value, (int) alpha * 255));
    }

    public float r() {
        return this.red / 255f;
    }

    public float g() {
        return this.green / 255f;
    }

    public float b() {
        return this.blue / 255f;
    }

    public float a() {
        return this.alpha / 255f;
    }

    public int argb() {
        return FastColor.ARGB32.color(this.alpha, this.red, this.green, this.blue);
    }

    public int argb(int alpha) {
        return FastColor.ARGB32.color(alpha, this.red, this.green, this.blue);
    }

    public int rgb() {
        return FastColor.ARGB32.color(this.red, this.green, this.blue);
    }

    public HSV hsv() {
        float[] vals = new float[3];
        Color.RGBtoHSB(this.red, this.green, this.blue, vals);
        return new HSV(vals[0], vals[1], vals[2]);
    }

    public record HSV(float hue, float saturation, float value) {}
}
