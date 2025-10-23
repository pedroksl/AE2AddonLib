package net.pedroksl.ae2addonlib.gui;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.ae2addonlib.api.ISetAmountMenuHost;
import net.pedroksl.ae2addonlib.registry.helpers.LibMenus;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.MenuLocator;
import appeng.menu.slot.InaccessibleSlot;
import appeng.util.inv.AppEngInternalInventory;

/**
 * The menu for a {@link net.pedroksl.ae2addonlib.client.screens.SetAmountScreen}.
 * Used as a sub menu for menus that implement the {@link ISetAmountMenuHost}.
 */
public class SetAmountMenu extends AEBaseMenu implements ISubMenu {

    private final ISubMenuHost host;

    private static final String ACTION_SET_STOCK_AMOUNT = "setStockAmount";
    private GenericStack stack;
    private Consumer<GenericStack> consumer;
    private final Slot itemSlot;
    private ISetAmountMenuHost menuToReturnTo;

    @GuiSync(1)
    private long initialAmount = -1;

    @GuiSync(2)
    private long maxAmount = -1;

    /**
     * Default constructor called when AE2 first initializes menus before configuring the instance.
     * @param id The menu id.
     * @param playerInventory The player's inventory.
     * @param host The menu's {@link ISubMenuHost}.
     */
    public SetAmountMenu(int id, Inventory playerInventory, ISubMenuHost host) {
        this(id, playerInventory, host, null);
    }

    /**
     * Constructs the class an initializes the slots and client actions.
     * @param id The menu id.
     * @param playerInventory The player's inventory.
     * @param host The menu's {@link ISubMenuHost}.
     * @param menuToReturnTo The menu to return to, must implement {@link ISetAmountMenuHost}.
     */
    public SetAmountMenu(
            int id, Inventory playerInventory, ISubMenuHost host, @Nullable ISetAmountMenuHost menuToReturnTo) {
        super(LibMenus.SET_AMOUNT.get(), id, playerInventory, host);
        this.host = host;
        this.menuToReturnTo = menuToReturnTo;

        this.itemSlot = new InaccessibleSlot(new AppEngInternalInventory(1), 0);
        this.addSlot(this.itemSlot, SlotSemantics.MACHINE_OUTPUT);

        registerClientAction(ACTION_SET_STOCK_AMOUNT, Long.class, this::confirm);
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }

    /**
     * Open function provided to be called by parent to initialize the menu with some parameters.
     * Sets the max amount to infinite and returns to the main menu as defined by the host.
     * @param player The server player.
     * @param locator The menu host locator.
     * @param stack The initial stack configuration.
     * @param consumer A method to be run after the user selects the desired amount.
     */
    public static void open(
            ServerPlayer player, MenuLocator locator, GenericStack stack, Consumer<GenericStack> consumer) {
        open(player, locator, stack, consumer, null, -1);
    }

    /**
     * Open function provided to be called by parent to initialize the menu with some parameters.
     * The max amount is configurable, Returns to menuToReturnTo.
     * @param player The server player.
     * @param locator The menu host locator.
     * @param stack The initial stack configuration.
     * @param consumer A method to be run after the user selects the desired amount.
     * @param menuToReturnTo The menu to return to. Must implement {@link ISetAmountMenuHost}.
     * @param maxAmount The max amount allowed for input.
     */
    public static void open(
            ServerPlayer player,
            MenuLocator locator,
            GenericStack stack,
            Consumer<GenericStack> consumer,
            ISetAmountMenuHost menuToReturnTo,
            long maxAmount) {
        MenuOpener.open(LibMenus.SET_AMOUNT.get(), player, locator);

        if (player.containerMenu instanceof SetAmountMenu cca) {
            cca.setStack(stack, maxAmount);
            cca.setConsumer(consumer);
            cca.setMenuToReturnTo(menuToReturnTo);
            cca.broadcastChanges();
        }
    }

    private void setStack(GenericStack stack, long maxAmount) {
        this.stack = Objects.requireNonNull(stack, "stack");
        this.initialAmount = stack.amount();
        this.maxAmount = maxAmount == -1 ? 64L * (long) stack.what().getAmountPerUnit() : maxAmount;
        this.itemSlot.set(stack.what().wrapForDisplayOrFilter());
    }

    private void setConsumer(Consumer<GenericStack> consumer) {
        this.consumer = consumer;
    }

    private void setMenuToReturnTo(ISetAmountMenuHost menuToReturnTo) {
        this.menuToReturnTo = menuToReturnTo;
    }

    /**
     * Handler for the "confirm" action on the client.
     * @param amount The amount configured in the screen.
     */
    public void confirm(long amount) {
        if (isClientSide()) {
            sendClientAction(ACTION_SET_STOCK_AMOUNT, amount);
            return;
        }

        if (amount <= 0L) {
            this.consumer.accept(null);
        } else {
            this.consumer.accept(new GenericStack(this.stack.what(), amount));
        }

        if (menuToReturnTo == null) {
            host.returnToMainMenu(getPlayer(), this);
        } else {
            this.menuToReturnTo.returnFromSetAmountMenu();
        }
    }

    /**
     * Getter for the initial amount.
     * @return The initial amount.
     */
    public long getInitialAmount() {
        return initialAmount;
    }

    /**
     * Getter for the maximum amount.
     * @return The max amount.
     */
    public long getMaxAmount() {
        return maxAmount;
    }

    /**
     * Getter for the current {@link GenericStack}.
     * @return The current stack.
     */
    @Nullable
    public AEKey getWhat() {
        var stack = GenericStack.fromItemStack(itemSlot.getItem());
        return stack != null ? stack.what() : null;
    }
}
