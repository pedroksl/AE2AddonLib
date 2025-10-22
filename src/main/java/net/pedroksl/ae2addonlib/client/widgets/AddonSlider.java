package net.pedroksl.ae2addonlib.client.widgets;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;
import appeng.core.AppEng;

/**
 * A configurable and extendable slider.
 */
public class AddonSlider implements ICompositeWidget {

    /**
     * Resource locations of the slider's textures
     */
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            AppEng.makeId("button"), AppEng.makeId("button_disabled"), AppEng.makeId("button_highlighted"));

    private static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HALF_WIDTH = 4;

    /**
     * True if the sliders is currently hovered, false otherwise.
     */
    protected boolean isHovered;

    /**
     * The top-left anchor position of the slider.
     */
    protected Point position = new Point(0, 0);
    /**
     * The width of the slider.
     */
    protected int width = 100;
    /**
     * The height of the slider.
     */
    protected int height = 20;
    /**
     * The alpha of the slider.
     */
    protected float alpha = 1.0F;

    /**
     * The current value of the slider.
     */
    protected double value = 0;
    /**
     * The minimum value of the slider.
     */
    protected double minValue = 0;
    /**
     * The maximum value of the slider.
     */
    protected double maxValue = 1;
    /**
     * The step size of the slider.
     */
    protected double stepSize = 1;
    /**
     * The display format of the numbers.
     */
    private final DecimalFormat format;

    private boolean isDragging = false;

    private boolean canChangeValue;

    private Consumer<Double> setter;

    /**
     * Default constructor. Won't do anything until the consumer is set with {@link #setCallback(Consumer)}.
     */
    public AddonSlider() {
        this(value -> {});
    }

    /**
     * Constructs a slider that calls a double consumer whenever the value is applied by the player.
     * @param setter The double consumer called when applying the value.
     */
    public AddonSlider(Consumer<Double> setter) {
        this.setter = setter;
        this.format = new DecimalFormat("0");
    }

    /**
     * Constructs a slider with a range of values and a consumer that is called whenever the player applies the value.
     * @param minValue The minimum accepted value.
     * @param maxValue The maximum accepted value.
     * @param currentValue The current value.
     * @param stepSize The step size.
     * @param setter The double consumer called when applying the value.
     */
    public AddonSlider(
            double minValue, double maxValue, double currentValue, double stepSize, Consumer<Double> setter) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        this.value = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.setter = setter;
        this.format = new DecimalFormat("0");
    }

    /**
     * Getter for the value as a double.
     * @return The current value as a double.
     */
    public double getValue() {
        return this.value * (this.maxValue - this.minValue) + this.minValue;
    }

    /**
     * Getter for the value as a long.
     * @return The current value as a long.
     */
    public long getValueLong() {
        return Math.round(this.getValue());
    }

    /**
     * Getter for the value as an int.
     * @return The current value as an int.
     */
    public int getValueInt() {
        return (int) this.getValueLong();
    }

    /**
     * Sets the current value.
     * @param value The desired current value.
     */
    public void setValue(double value) {
        this.setFractionalValue((value - this.minValue) / (this.maxValue - this.minValue));
    }

    /**
     * Getter for the value as a formatted string.
     * @return The current value as a formatted string.
     */
    public String getValueString() {
        return this.format.format(this.getValue());
    }

    /**
     * Convenience methods that set the current value as well as the limits.
     * @param minValue The minimum accepted value.
     * @param maxValue The maximum accepted value.
     * @param currentValue The current value.
     * @param stepSize The step size.
     */
    public void setValues(double minValue, double maxValue, double currentValue, float stepSize) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = stepSize;
        setValue(currentValue);
    }

    /**
     * Sets the callback function, called when applying the value.
     * @param setter The double consumer to call when applying the value.
     */
    public void setCallback(Consumer<Double> setter) {
        this.setter = setter;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var mouseX = bounds.getX() + mouse.getX();
        var mouseY = bounds.getY() + mouse.getY();

        var minX = bounds.getX() + this.position.getX();
        var minY = bounds.getY() + this.position.getY();

        this.isHovered = guiGraphics.containsPointInScissor(mouseX, mouseY)
                && mouseX >= minX
                && mouseY >= minY
                && mouseX < minX + this.width
                && mouseY < minY + this.height;

        renderWidget(guiGraphics, new Point(minX, minY), mouse);
    }

    /**
     * Renders the slider and its background.
     * @param guiGraphics The gui graphics
     * @param topLeft Top-left anchors point.
     * @param mouse Mouse position.
     */
    protected void renderWidget(GuiGraphics guiGraphics, Point topLeft, Point mouse) {
        var minX = topLeft.getX();
        var minY = topLeft.getY();

        var heightUsedForText = 10;
        var minSliderY = minY + heightUsedForText;
        var sliderHeight = this.height - heightUsedForText;

        var maxX = minX + this.width;
        var maxY = minY + this.height;

        var middleX = minX + this.width / 2;
        var middleY = minSliderY + sliderHeight / 2;

        var backColor = Colors.LIGHT_GRAY.argb((int) (this.alpha * 255f));
        var lineColor = Colors.DARK_GRAY_BLUE.argb((int) (this.alpha * 255f));

        // Render background rectangles
        guiGraphics.fill(minX - 2, minSliderY - 2, maxX + 2, maxY + 2, Color.WHITE.getRGB());
        guiGraphics.fill(minX - 1, minSliderY - 1, maxX + 1, maxY + 1, backColor);

        // Render guide lines for the slider
        guiGraphics.hLine(minX, maxX - 1, middleY, lineColor);
        guiGraphics.vLine(minX, minSliderY, maxY, lineColor);
        guiGraphics.vLine(maxX - 1, minSliderY, maxY, lineColor);

        // Render min/max text values
        Font font = Minecraft.getInstance().font;
        String minText = getValueText(this.minValue);
        guiGraphics.drawString(font, minText, minX, minY, lineColor, false);
        String maxText = getValueText(this.maxValue);
        guiGraphics.drawString(font, maxText, maxX - font.width(maxText), minY, lineColor, false);

        // Render current value text
        String currentText = getValueText(this.getValue());
        guiGraphics.drawString(font, currentText, middleX - font.width(currentText) / 2, minY, lineColor, false);

        // Render the handle
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        guiGraphics.blitSprite(
                this.getHandleSprite(),
                minX + (int) (this.value * (double) (this.width - HANDLE_WIDTH)),
                minSliderY - 1,
                HANDLE_WIDTH,
                this.height - 8);
    }

    private String getValueText(double value) {
        if (this.stepSize < 1) return String.format("%.1f", value);
        else {
            return String.format("%.0f", value);
        }
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        this.isDragging = true;
        this.playDownSound();
        setValueFromMouse(mousePos.getX());
        return true;
    }

    private void playDownSound() {
        var handler = Minecraft.getInstance().getSoundManager();
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean onMouseUp(Point mousePos, int button) {
        this.isDragging = false;
        return false;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        if (delta > 0) {
            setValue(Math.min(this.maxValue, getValue() + this.stepSize));
        } else {
            setValue(Math.max(this.minValue, getValue() - this.stepSize));
        }
        return true;
    }

    @Override
    public boolean wantsAllMouseUpEvents() {
        return true;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        if (this.isDragging) {
            setValueFromMouse(mousePos.getX());
            return true;
        }
        return false;
    }

    private void setValueFromMouse(double mouseX) {
        this.setFractionalValue((mouseX - (double) (this.position.getX())) / (double) (this.width));
    }

    private void setFractionalValue(double fractionalValue) {
        double oldValue = this.value;
        this.value = this.snapToNearest(Math.clamp(fractionalValue, 0f, 1f));
        if (!Mth.equal(oldValue, this.value)) {
            this.applyValue();
        }
    }

    private double snapToNearest(double value) {
        if (this.stepSize <= (double) 0.0F) {
            return Mth.clamp(value, 0.0F, 1.0F);
        } else {
            value = Mth.lerp(Mth.clamp(value, 0.0F, 1.0F), this.minValue, this.maxValue);
            value = this.stepSize * (double) Math.round(value / this.stepSize);
            if (this.minValue > this.maxValue) {
                value = Mth.clamp(value, this.maxValue, this.minValue);
            } else {
                value = Mth.clamp(value, this.minValue, this.maxValue);
            }

            return Mth.map(value, this.minValue, this.maxValue, 0.0F, 1.0F);
        }
    }

    /**
     * Applies the value using the member consumer.
     */
    protected void applyValue() {
        setter.accept(this.getValue());
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(this.position.getX(), this.position.getY(), this.width, this.height);
    }

    /**
     * Gets the correct texture based on the current state.
     * @return The selected texture's {@link ResourceLocation}.
     */
    protected @NotNull ResourceLocation getHandleSprite() {
        return SPRITES.get(this.isVisible(), this.isHovered);
    }
}
