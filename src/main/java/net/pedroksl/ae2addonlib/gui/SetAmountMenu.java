package net.pedroksl.ae2addonlib.gui;

import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

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
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.slot.InaccessibleSlot;
import appeng.util.inv.AppEngInternalInventory;

public class SetAmountMenu extends AEBaseMenu implements ISubMenu {

    private final ISubMenuHost host;

    public static final String ACTION_SET_STOCK_AMOUNT = "setStockAmount";
    private GenericStack stack;
    private Consumer<GenericStack> consumer;
    private final Slot itemSlot;
    private ISetAmountMenuHost menuToReturnTo;

    @GuiSync(1)
    private long initialAmount = -1;

    @GuiSync(2)
    private long maxAmount = -1;

    public SetAmountMenu(int id, Inventory playerInventory, ISubMenuHost host) {
        this(id, playerInventory, host, null);
    }

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

    public static void open(
            ServerPlayer player, MenuHostLocator locator, GenericStack stack, Consumer<GenericStack> consumer) {
        open(player, locator, stack, consumer, null, -1);
    }

    public static void open(
            ServerPlayer player,
            MenuHostLocator locator,
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

    public long getInitialAmount() {
        return initialAmount;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    @Nullable
    public AEKey getWhat() {
        var stack = GenericStack.fromItemStack(itemSlot.getItem());
        return stack != null ? stack.what() : null;
    }
}
