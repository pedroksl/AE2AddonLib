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
            var item = this.getItemOverlay();

            if (this.halfSize) {
                this.width = 8;
                this.height = 8;
            }

            var yOffset = isHovered() && this.enableHoverOffset ? 1 : 0;

            if (this.halfSize) {
                if (!disableBackground) {
                    Icon.TOOLBAR_BUTTON_BACKGROUND
                            .getBlitter()
                            .dest(getX(), getY())
                            .zOffset(10)
                            .blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.renderItem(new ItemStack(item), getX(), getY(), 0, 20);
                } else if (icon != null) {
                    Blitter blitter = icon.getBlitter();
                    if (!this.active) {
                        blitter.opacity(0.5f);
                    }
                    blitter.dest(getX(), getY()).zOffset(20).blit(guiGraphics);
                }
            } else {
                if (!disableBackground) {
                    Icon bgIcon = isHovered()
                            ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                            : isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;

                    bgIcon.getBlitter()
                            .dest(getX() - 1, getY() + yOffset, 18, 20)
                            .zOffset(2)
                            .blit(guiGraphics);
                }
                if (item != null) {
                    guiGraphics.renderItem(new ItemStack(item), getX(), getY() + 1 + yOffset, 0, 3);
                } else if (icon != null) {
                    icon.getBlitter()
                            .dest(getX(), getY() + 1 + yOffset)
                            .zOffset(3)
                            .blit(guiGraphics);
                }
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
