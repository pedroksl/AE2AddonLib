package net.pedroksl.ae2addonlib.client.widgets;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ConfirmableTextField;

/**
 * Widget that creates a text input designed to receive hex color codes. It will accept up to 7 characters to house a possible "#" symbol.
 * Alpha values are not accepted as input for this widget.
 * This widget is part of {@link ColorPicker}.
 */
public class HexColorInput extends ConfirmableTextField {

    private final HexFormat hexFormat = HexFormat.of();

    /**
     * Constructs the hex color input with chosen parameters.
     * @param style The screen style.
     * @param fontRenderer The font renderer, usually taken from {@link Minecraft#font}
     * @param x The left-most coordinate of the widget.
     * @param y The top-most coordinate of the widget.
     * @param width The width of the widget.
     * @param height The height of the widget.
     * @param onConfirm The consumer to be executed when applying the values.
     */
    public HexColorInput(
            ScreenStyle style, Font fontRenderer, int x, int y, int width, int height, Consumer<Integer> onConfirm) {
        super(style, fontRenderer, x, y, width, height);

        setBordered(false);
        setMaxLength(7);
        setTextColor(16777215);
        setSelectionColor(-16777088);
        setVisible(true);
        setResponder(text -> this.validate());
        setOnConfirm(() -> {
            var opt = getIntValue();
            if (opt.isPresent()) {
                onConfirm.accept(opt.getAsInt());
            }
        });
        this.validate();
    }

    /**
     * Getter for the current value as an {@link OptionalInt}.
     * Optional was used here because the hex code might not be valid at all times.
     * @return The int value wrapped in an optional.
     */
    public OptionalInt getIntValue() {
        var value = getValue();
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        try {
            var intValue = Integer.parseInt(value, 16);
            return OptionalInt.of(intValue);
        } catch (NumberFormatException exception) {
            return OptionalInt.empty();
        }
    }

    /**
     * Setter for the hex code via color as an int.
     * @param color The color to set.
     */
    public void setColor(int color) {
        setValue(hexFormat.toHexDigits(color).substring(2));
    }

    private void validate() {
        List<Component> validation = new ArrayList<>();
        if (getIntValue().isEmpty()) {
            validation.add(LibText.InvalidHexInput.text());
        }
        setTooltipMessage(validation);
    }
}
