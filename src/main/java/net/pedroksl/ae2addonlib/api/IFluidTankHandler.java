package net.pedroksl.ae2addonlib.api;

import org.lwjgl.glfw.GLFW;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;

/**
 * Interface used in menus that contain {@link net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot}s.
 * Attaches the handler directly to the menu.
 */
public interface IFluidTankHandler {

    /**
     * Getter for the server player.
     * @return The server player.
     */
    ServerPlayer getServerPlayer();

    /**
     * Getter for the carried item.
     * @return The carried item.
     */
    ItemStack getCarriedItem();

    /**
     * Setter for the carried item.
     * @param stack The carried item.
     */
    void setCarriedItem(ItemStack stack);

    /**
     * Getter for the tank.
     * @return The tank.
     */
    GenericStackInv getTank();

    /**
     * Checks if the current tank can be extracted from.
     * @param index The tank index.
     * @return If it can be extracted from.
     */
    boolean canExtractFromTank(int index);

    /**
     * Checks if the current tank can be inserted into.
     * @param index The tank index.
     * @return If it can be inserted into.
     */
    boolean canInsertInto(int index);

    /**
     * Handles item usage relating to the tank. Will try to fill/empty containers, depending on the button used to click.
     * @param index The tank index.
     * @param button The button used to interact with the tank.
     */
    default void onItemUse(int index, int button) {
        var stack = getCarriedItem();
        if (!stack.isEmpty()) {
            var forgeCap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            forgeCap.ifPresent(cap -> {
                var tank = getTank();
                if (tank == null) return;

                boolean isBucket = stack.getItem() instanceof BucketItem;
                if ((!isBucket && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                        || (isBucket && ((BucketItem) stack.getItem()).getFluid() == Fluids.EMPTY)) {
                    if (!canExtractFromTank(index)) return;

                    var genStack = tank.getStack(index);
                    if (genStack != null && genStack.what() != null) {
                        var fluid = ((AEFluidKey) genStack.what()).toStack((int) genStack.amount());

                        var extracted = Math.min(genStack.amount(), 1000);
                        var inserted = cap.fill(
                                new FluidStack(fluid.getFluid(), (int) extracted), IFluidHandler.FluidAction.EXECUTE);
                        var endAmount = genStack.amount() - inserted;
                        if (endAmount > 0) {
                            tank.setStack(index, new GenericStack(genStack.what(), genStack.amount() - inserted));
                        } else {
                            tank.setStack(index, null);
                        }

                        setCarriedItem(cap.getContainer());

                        if (inserted > 0) {
                            FluidTankSlot.playDownSound(true);
                        }
                    }
                } else {
                    if (!canInsertInto(index) || cap.getFluidInTank(0).isEmpty()) return;

                    var fluid = cap.getFluidInTank(0);
                    var genStack = GenericStack.fromFluidStack(fluid);
                    if (genStack != null && genStack.what() != null) {

                        if (!cap.drain(
                                        (int) tank.insert(index, genStack.what(), 1000, Actionable.SIMULATE),
                                        IFluidHandler.FluidAction.SIMULATE)
                                .isEmpty()) {

                            var extracted = cap.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                                    .getAmount();
                            var inserted = tank.insert(index, genStack.what(), extracted, Actionable.MODULATE);
                            if (extracted - inserted > 0) {
                                cap.fill(
                                        new FluidStack(fluid.getFluid(), (int) (extracted - inserted)),
                                        IFluidHandler.FluidAction.EXECUTE);
                            }

                            setCarriedItem(cap.getContainer());

                            FluidTankSlot.playDownSound(false);
                        }
                    }
                }
            });
        }
    }
}
