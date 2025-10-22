package net.pedroksl.ae2addonlib.util;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import appeng.api.features.HotkeyAction;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

/**
 * A hotkey action that will try to match an {@link ItemStack} with the equipped armor pieces  of the target player.
 * @param locatable The {@link Predicate} of the desired item stack.
 * @param opener The runnable that should be executed if the matching stack is found.
 */
public record ArmorHotkeyAction(Predicate<ItemStack> locatable, Opener opener) implements HotkeyAction {

    /**
     * Convenience constructor that takes an {@link ItemLike} and wraps it in a predicate
     * @param item The item to be used in the predicate.
     * @param opener The runnable that should be executed if the matching stack is found.
     */
    public ArmorHotkeyAction(ItemLike item, Opener opener) {
        this((stack) -> stack.is(item.asItem()), opener);
    }

    @Override
    public boolean run(Player player) {
        var items = player.getArmorSlots();
        int i = 0;
        for (var item : items) {
            if (this.locatable.test(item)) {
                if (opener.open(player, MenuLocators.forInventorySlot(Inventory.INVENTORY_SIZE + i))) {
                    return true;
                }
            }
            i++;
        }

        return false;
    }

    /**
     * Wrapper for the runnable that happens when the locatable matched the equipped item.
     */
    @FunctionalInterface
    public interface Opener {
        /**
         * The opener's method.
         * @param olayer The player that triggered the hotkey.
         * @param menuHostLocator The menu locator.
         * @return Returns if the actions was successful.
         */
        boolean open(Player olayer, ItemMenuHostLocator menuHostLocator);
    }
}
