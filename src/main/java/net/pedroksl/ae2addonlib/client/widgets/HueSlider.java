package net.pedroksl.ae2addonlib.client.widgets;

import java.awt.*;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.client.Point;

/**
 * Widget that creates a slider and lets the user select a hue and, along with {@link SaturationValuePicker}, is used to
 * compose a color in the HSV color space.
 * This widget is part of {@link ColorPicker}.
 */
public class HueSlider extends AddonSlider {

    /**
     * Constructs a default hue slider with a consumer function to be called when applying values.
     * @param setter The setter to be called when applying values.
     */
    public HueSlider(Consumer<Double> setter) {
        this(0f, setter);
    }

    /**
     * Constructs a hue slider with a selected initial hue and a consumer function to be called when applying values.
     * @param hue The initial hue.
     * @param setter The setter to be called when applying values.
     */
    public HueSlider(float hue, Consumer<Double> setter) {
        super(0f, 360f, hue * 360f, 1f, setter);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, Point topLeft, Point mouse) {
        var minX = topLeft.getX();
        var minY = topLeft.getY();
        var w = this.width;
        var h = this.height;

        var lineColor = 4276052 | (255 << 24);
        guiGraphics.fill(minX - 1, minY - 1, minX + w + 1, minY + h + 1, lineColor);
        for (var i = 0; i < w; i++) {
            var hue = (float) i / w;
            guiGraphics.fill(
                    minX + i,
                    minY,
                    minX + i + 1,
                    minY + h,
                    Colors.ofHsv(hue, 1f, 1f).argb());
        }

        var handleX = minX + (int) (this.value * (double) (this.width - 1));
        var handleY = minY - 1;
        var handleMaxY = handleY + h + 1;
        guiGraphics.hLine(handleX - 1, handleX + 1, handleY, Color.WHITE.getRGB());
        guiGraphics.hLine(handleX - 1, handleX + 1, handleMaxY, Color.WHITE.getRGB());
        guiGraphics.vLine(handleX - 1, handleY, handleMaxY, Color.WHITE.getRGB());
        guiGraphics.vLine(handleX + 1, handleY, handleMaxY, Color.WHITE.getRGB());
    }
}
