package net.pedroksl.ae2addonlib.client.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;

import appeng.menu.slot.ResizableSlot;

public class ScreenUtil {
    public static void renderSlotHighlight(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, Slot slot, int highlightColor) {
        if (!slot.isHighlightable()) {
            return;
        }

        int x = offsetX + slot.x;
        int y = offsetY + slot.y;
        int w, h;
        if (slot instanceof ResizableSlot resizableSlot) {
            w = resizableSlot.getWidth();
            h = resizableSlot.getHeight();
        } else {
            w = 16;
            h = 16;
        }

        // Same as the Vanilla method, just with dynamic width and height
        // Added a custom slot highlight effect - RID
        guiGraphics.horizontalLine(x, x + w, y - 1, 0xFFdaffff);
        guiGraphics.horizontalLine(x - 1, x + w, y + h, 0xFFdaffff);
        guiGraphics.verticalLine(x - 1, y - 2, y + h, 0xFFdaffff);
        guiGraphics.verticalLine(x + w, y - 2, y + h, 0xFFdaffff);
        guiGraphics.fillGradient(x, y, x + w, y + h, highlightColor, highlightColor);
    }
}
