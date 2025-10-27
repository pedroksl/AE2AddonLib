package net.pedroksl.ae2addonlib.client.widgets;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.api.orientation.RelativeSide;

/**
 * Specific button type used in {@link net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen} to provide
 * Enable/Disable behavior and render the related tile entity.
 */
public class OutputDirectionButton extends Button {

    private ItemStack item;
    private RelativeSide side;
    private boolean enabled = false;
    private final LibIcons enabledIcon = LibIcons.TOOLBAR_BUTTON_ENABLED;
    private final LibIcons disabledIcon = LibIcons.TOOLBAR_BUTTON_BACKGROUND;

    /**
     * Constructs an output direction button with initial values.
     * @param x The left-most coordinate of the button.
     * @param y The top-most coordinate of the button.
     * @param width The width of the button.
     * @param height The height of the button.
     * @param onPress The method to be executed when applying new values.
     */
    public OutputDirectionButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    /**
     * Setter for the enabled state of the button.
     * @param isEnabled If the button is enabled.
     */
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
        setTooltip(Tooltip.create(enabled ? LibText.Enabled.text() : LibText.Disabled.text()));
    }

    /**
     * Setter for the relative side of this button.
     * @param side The relative side of the button.
     */
    public void setSide(RelativeSide side) {
        this.side = side;
    }

    /**
     * Setter for the item stack to be rendered in the button.
     * @param item The item stack to render.
     */
    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    /**
     * Getter for the relative side of the button.
     * @return The relative side of the button.
     */
    public RelativeSide getSide() {
        return side;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        var blitter = enabled ? enabledIcon : disabledIcon;
        blitter.getBlitter().dest(getX(), getY()).blit(pGuiGraphics);

        if (item != null) {
            pGuiGraphics.renderItem(item, this.getX() + 1, this.getY() + 1);
        }
    }
}
