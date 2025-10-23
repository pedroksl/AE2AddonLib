/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package net.pedroksl.ae2addonlib.client.widgets;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.ITooltip;

/**
 * This button is almost a 1:1 copy of AE2's {@link appeng.client.gui.widgets.IconButton}.
 * The difference is that this version uses an {@link IBlitterIcon}. This change opens this button implementation
 * for Addons to create their own Appearances without needing to reuse AE2's {@link Icon} for textures.
 */
public abstract class AddonIconButton extends Button implements ITooltip {

    private boolean halfSize = false;

    private boolean disableClickSound = false;

    private boolean disableBackground = false;

    private boolean enableHoverOffset = true;

    /**
     * Constructs a button with a callback function.
     * @param onPress The callback function.
     */
    public AddonIconButton(OnPress onPress) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    /**
     * Changes the button's visibility.
     * @param vis The desired visibility.
     */
    public void setVisibility(boolean vis) {
        this.visible = vis;
        this.active = vis;
    }

    /**
     * Enable or disable the offset when hovering the button.
     * @param enable Enable/disable the behavior.
     */
    public void setHoverOffsetChange(boolean enable) {
        this.enableHoverOffset = enable;
    }

    @Override
    public void playDownSound(SoundManager soundHandler) {
        if (!disableClickSound) {
            super.playDownSound(soundHandler);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {

        if (this.visible) {
            var icon = this.getIcon();

            Blitter blitter = icon.getBlitter();
            if (!this.active) {
                blitter.opacity(0.5f);
            }

            if (this.halfSize) {
                this.width = 8;
                this.height = 8;
            }

            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend(); // FIXME: This should be the _default_ state, but some vanilla widget disables

            if (isFocused()) {
                // Draw 1px border with 4 quads, don't rely on the background as it can be disabled.
                // top
                guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY(), 0xFFFFFFFF);
                // left
                guiGraphics.fill(getX() - 1, getY(), getX(), getY() + height, 0xFFFFFFFF);
                // right
                guiGraphics.fill(getX() + width, getY(), getX() + width + 1, getY() + height, 0xFFFFFFFF);
                // bottom
                guiGraphics.fill(getX() - 1, getY() + height, getX() + width + 1, getY() + height + 1, 0xFFFFFFFF);
            }

            if (this.halfSize) {
                var pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(getX(), getY(), 0.0F);
                pose.scale(0.5f, 0.5f, 1.f);

                if (!disableBackground) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND.getBlitter().dest(0, 0).blit(guiGraphics);
                }
                blitter.dest(0, 0).blit(guiGraphics);
                pose.popPose();
            } else {
                if (!disableBackground) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND
                            .getBlitter()
                            .dest(getX(), getY())
                            .blit(guiGraphics);
                }
                icon.getBlitter().dest(getX(), getY()).blit(guiGraphics);
            }
            RenderSystem.enableDepthTest();

            var item = this.getItemOverlay();
            if (item != null) {
                guiGraphics.renderItem(new ItemStack(item), getX(), getY());
            }
        }
    }

    /**
     * Getter for the button's icon.
     * @return The button's icon.
     */
    protected abstract IBlitterIcon getIcon();

    /**
     * Prioritized over {@link #getIcon()} if not null.
     * @return The item used as overlay. Null if using an icon instead.
     */
    @Nullable
    protected Item getItemOverlay() {
        return null;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), this.halfSize ? 8 : 16, this.halfSize ? 8 : 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return this.visible;
    }

    /**
     * Getter for the button's half size property.
     * @return If the button is half size.
     */
    public boolean isHalfSize() {
        return this.halfSize;
    }

    /**
     * Setter for the button's half size property.
     * @param halfSize Should the button be half size.
     */
    public void setHalfSize(boolean halfSize) {
        this.halfSize = halfSize;
    }

    /**
     * Checks if the button has click sounds enabled.
     * @return If the button's click sounds are enabled.
     */
    public boolean isDisableClickSound() {
        return disableClickSound;
    }

    /**
     * Enables/disables the button's click sound.
     * @param disableClickSound Should the button be enabled or disabled.
     */
    public void setDisableClickSound(boolean disableClickSound) {
        this.disableClickSound = disableClickSound;
    }

    /**
     * Checks if the button's background rendering is enabled/disabled.
     * @return If the button's background rendering is enabled or disabled.
     */
    public boolean isDisableBackground() {
        return disableBackground;
    }

    /**
     * Enables/disables the background rendering.
     * @param disableBackground Should the background be rendered.
     */
    public void setDisableBackground(boolean disableBackground) {
        this.disableBackground = disableBackground;
    }
}
