package net.pedroksl.ae2addonlib.client.widgets;

import java.awt.*;
import java.util.function.BiConsumer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.client.Point;
import appeng.client.gui.ICompositeWidget;

/**
 * Widget that creates a two axis slider and lets the user select a coordinate. The coordinate is then converted to saturation
 * and value and, along with {@link HueSlider}, used to compose a color in the HSV color space.
 * This widget is part of {@link ColorPicker}.
 */
public class SaturationValuePicker implements ICompositeWidget {

    private Point position;
    private int width;
    private int height;

    private float hue;
    private float saturation;
    private float value;

    private final BiConsumer<Float, Float> saturationAndValueSetter;

    private boolean isDragging = false;

    /**
     * Constructs a saturation value icker with initial values and a {@link BiConsumer} called when applying values.
     * @param hue The initial hue.
     * @param saturation The initial saturation.
     * @param value The initial value.
     * @param saturationAndValueSetter The function called when applying values.
     */
    public SaturationValuePicker(
            float hue, float saturation, float value, BiConsumer<Float, Float> saturationAndValueSetter) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;

        this.saturationAndValueSetter = saturationAndValueSetter;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor guiGraphics, Rect2i bounds, Point mouse) {
        var minX = bounds.getX() + this.position.getX();
        var minY = bounds.getY() + this.position.getY();
        var w = this.width;
        var h = this.height;

        var lineColor = 4276052 | (255 << 24);
        guiGraphics.fill(minX - 1, minY - 1, minX + w + 1, minY + h + 1, lineColor);

        guiGraphics.submitGuiElementRenderState(new MultiGradientRectangleRenderState(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                new Matrix3x2f(guiGraphics.pose()),
                minX,
                minY,
                minX + w,
                minY + h,
                Colors.ofHsv(hue, 0f, 1f).argb(),
                Colors.ofHsv(hue, 1f, 1f).argb(),
                Colors.ofHsv(hue, 0f, 0f).argb(),
                Colors.ofHsv(hue, 1f, 0f).argb(),
                guiGraphics.peekScissorStack()));

        int hsvX = minX + (int) (this.saturation * (this.width - 1));
        int hsvY = minY + (int) ((1f - this.value) * (this.height - 1));
        guiGraphics.horizontalLine(hsvX - 1, hsvX + 1, hsvY - 1, Color.WHITE.getRGB());
        guiGraphics.horizontalLine(hsvX - 1, hsvX + 1, hsvY + 1, Color.WHITE.getRGB());
        guiGraphics.verticalLine(hsvX - 1, hsvY - 1, hsvY + 1, Color.WHITE.getRGB());
        guiGraphics.verticalLine(hsvX + 1, hsvY - 1, hsvY + 1, Color.WHITE.getRGB());
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        this.isDragging = true;
        this.playDownSound();
        setValueFromMouse(mousePos.getX(), mousePos.getY());
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
    public boolean wantsAllMouseUpEvents() {
        return true;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int button) {
        if (this.isDragging) {
            setValueFromMouse(mousePos.getX(), mousePos.getY());
            return true;
        }
        return false;
    }

    private void setValueFromMouse(double mouseX, double mouseY) {
        double x = ((mouseX - (double) (this.position.getX())) / (double) (this.width));
        double y = ((mouseY - (double) (this.position.getY())) / (double) (this.height));

        double newSaturation = Math.clamp(x, 0f, 1f);
        double newValue = Math.clamp((1f - y), 0f, 1f);

        this.applyValue((float) newSaturation, (float) newValue);
    }

    /**
     * Applies the current saturation and value using the store {@link BiConsumer}.
     * @param newSaturation The new saturation.
     * @param newValue The new value.
     */
    protected void applyValue(float newSaturation, float newValue) {
        if (!Mth.equal(this.saturation, newSaturation) || !Mth.equal(this.value, newValue)) {
            this.saturation = newSaturation;
            this.value = newValue;
            this.saturationAndValueSetter.accept(this.saturation, this.value);
        }
    }

    /**
     * Setter for the hue. Affects the color space of the widget.
     * @param hue The new hue.
     */
    public void setHue(float hue) {
        this.hue = hue;
    }

    /**
     * Setter for the values.
     * @param saturation The new saturation.
     * @param value The new value.
     */
    public void setValues(float saturation, float value) {
        this.saturation = saturation;
        this.value = value;
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

    private record MultiGradientRectangleRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2fc pose,
            int x0,
            int y0,
            int x1,
            int y1,
            int tlColor,
            int trColor,
            int blColor,
            int brColor,
            @Nullable ScreenRectangle scissorArea,
            @Nullable ScreenRectangle bounds)
            implements GuiElementRenderState {
        public MultiGradientRectangleRenderState(
                RenderPipeline pipeline,
                TextureSetup textureSetup,
                Matrix3x2fc pose,
                int x0,
                int y0,
                int x1,
                int y1,
                int tlColor,
                int trColor,
                int blColor,
                int brColor,
                @Nullable ScreenRectangle scissorArea) {
            this(
                    pipeline,
                    textureSetup,
                    pose,
                    x0,
                    y0,
                    x1,
                    y1,
                    tlColor,
                    trColor,
                    blColor,
                    brColor,
                    scissorArea,
                    getBounds(x0, y0, x1, y1, pose, scissorArea));
        }

        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {
            vertexConsumer
                    .addVertexWith2DPose(this.pose(), (float) x1, (float) y0)
                    .setColor(trColor);
            vertexConsumer
                    .addVertexWith2DPose(this.pose(), (float) x0, (float) y0)
                    .setColor(tlColor);
            vertexConsumer
                    .addVertexWith2DPose(this.pose(), (float) x0, (float) y1)
                    .setColor(blColor);
            vertexConsumer
                    .addVertexWith2DPose(this.pose(), (float) x1, (float) y1)
                    .setColor(brColor);
        }

        private static @Nullable ScreenRectangle getBounds(
                int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
            ScreenRectangle bounds = (new ScreenRectangle(x0, y0, x1 - x0, y1 - y0)).transformMaxBounds(pose);
            return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
        }
    }
}
