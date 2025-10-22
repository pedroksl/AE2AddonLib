package net.pedroksl.ae2addonlib.client.screens;

import java.util.*;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionButton;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionItems;
import net.pedroksl.ae2addonlib.client.widgets.OutputDirectionButton;
import net.pedroksl.ae2addonlib.gui.OutputDirectionMenu;

import appeng.api.orientation.RelativeSide;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

/**
 * Screen used to interact with a {@link OutputDirectionMenu}.
 * This screen renders buttons for all directions as well as the block entities present in those directions.
 * It's used to allow the user to enable/disable specific sides for output.
 */
public class OutputDirectionScreen extends AEBaseScreen<OutputDirectionMenu> {

    private static final int BUTTON_WIDTH = 18;
    private static final int BUTTON_HEIGHT = 20;

    private static final int BUTTON_TOP_OFFSET = 6;
    private static final int BUTTON_LEFT_OFFSET = 7;
    private static final int BUTTON_OFFSET = 2;

    private final List<OutputDirectionButton> buttons = new ArrayList<>();
    private static final Map<RelativeSide, Point> BUTTON_POSITION = makeButtonPositionMap();

    /**
     * Constructs the screen and adds the button widgets.
     * @param menu The parent menu
     * @param playerInventory The player's inventory.
     * @param title The screen tytle.
     * @param style The screen style.
     */
    public OutputDirectionScreen(
            OutputDirectionMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        for (var side : RelativeSide.values()) {
            var pos = BUTTON_POSITION.get(side);
            var button = new OutputDirectionButton(
                    this.leftPos + pos.getX(),
                    this.topPos + pos.getY(),
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    this::buttonPressed);
            button.setSide(side);
            this.buttons.add(button);
            this.addRenderableWidget(button);
        }

        AESubScreen.addBackButton(menu, "back", widgets);

        AddonActionButton clearBtn = new AddonActionButton(AddonActionItems.CLEAR, menu::clearSides);
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearAll", clearBtn);
    }

    @Override
    protected void init() {
        super.init();

        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                var pos = BUTTON_POSITION.get(button.getSide());
                button.setPosition(this.leftPos + pos.getX(), this.topPos + pos.getY());
            }
        }
    }

    /**
     * Packet handler for the {@link net.pedroksl.ae2addonlib.network.clientPacket.OutputDirectionUpdatePacket}.
     * Updates all buttons according to server state.
     * @param sides A set of enabled sides.
     */
    public void update(Set<RelativeSide> sides) {
        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                button.setEnabled(sides.contains(side));
            }
        }
    }

    @Override
    protected void updateBeforeRender() {
        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                var pos = BUTTON_POSITION.get(side);
                button.setPosition(this.leftPos + pos.getX(), this.topPos + pos.getY());

                ItemStack item = this.getMenu().getAdjacentBlock(side);
                button.setItemStack(item);
            }
        }

        super.updateBeforeRender();
    }

    private static Map<RelativeSide, Point> makeButtonPositionMap() {
        Map<RelativeSide, Point> map = new HashMap<>(6);
        map.put(
                RelativeSide.FRONT,
                new Point(
                        BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET,
                        BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET));
        map.put(
                RelativeSide.BACK,
                new Point(
                        BUTTON_LEFT_OFFSET + 2 * BUTTON_WIDTH + 2 * BUTTON_OFFSET,
                        BUTTON_TOP_OFFSET + 2 * BUTTON_HEIGHT + 2 * BUTTON_OFFSET));
        map.put(RelativeSide.TOP, new Point(BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET, BUTTON_TOP_OFFSET));
        map.put(RelativeSide.RIGHT, new Point(BUTTON_LEFT_OFFSET, BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET));
        map.put(
                RelativeSide.BOTTOM,
                new Point(
                        BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET,
                        BUTTON_TOP_OFFSET + 2 * BUTTON_HEIGHT + 2 * BUTTON_OFFSET));
        map.put(
                RelativeSide.LEFT,
                new Point(
                        BUTTON_LEFT_OFFSET + 2 * BUTTON_WIDTH + 2 * BUTTON_OFFSET,
                        BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET));
        return map;
    }

    private void buttonPressed(Button b) {
        if (b instanceof OutputDirectionButton button) {
            var side = button.getSide();
            if (side != null) {
                this.getMenu().updateSideStatus(button.getSide());
            }
        }
    }
}
