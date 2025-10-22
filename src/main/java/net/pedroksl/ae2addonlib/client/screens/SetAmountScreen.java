package net.pedroksl.ae2addonlib.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.ae2addonlib.gui.SetAmountMenu;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.core.localization.GuiText;

/**
 * Screen used to interact with a {@link SetAmountMenu}.
 * This screen contains a widget to collect user input of a quantity of a stack and send it to the parent menu.
 */
public class SetAmountScreen extends AEBaseScreen<SetAmountMenu> {

    private final NumberEntryWidget amount;

    private boolean amountInitialized;

    /**
     * Constructs the screen and adds the widgets. The {@link NumberEntryWidget} is initialized with dummy values since
     * the actual values are not available until later.
     * @param menu The parent menu.
     * @param playerInventory The player's inventory.
     * @param title The screen title.
     * @param style The screen style.
     */
    public SetAmountScreen(SetAmountMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        widgets.addButton("save", GuiText.Set.text(), this::confirm);

        AESubScreen.addBackButton(menu, "back", widgets);

        this.amount = widgets.addNumberEntryWidget("amount", NumberEntryType.UNITLESS);
        this.amount.setLongValue(1);
        this.amount.setTextFieldStyle(style.getWidget("amountInput"));
        this.amount.setMinValue(0);
        this.amount.setHideValidationIcon(true);
        this.amount.setOnConfirm(this::confirm);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        if (!this.amountInitialized) {
            var what = menu.getWhat();
            if (what != null) {
                this.amount.setType(NumberEntryType.of(what));
                this.amount.setLongValue(menu.getInitialAmount());

                this.amount.setMaxValue(menu.getMaxAmount());
                this.amountInitialized = true;
            }
        }
    }

    private void confirm() {
        this.amount.getLongValue().ifPresent(menu::confirm);
    }
}
