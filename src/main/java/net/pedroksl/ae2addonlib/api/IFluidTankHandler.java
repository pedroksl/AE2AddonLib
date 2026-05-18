package net.pedroksl.ae2addonlib.api;

import org.lwjgl.glfw.GLFW;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankClientAudioPacket;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;

/**
 * Interface used in menus that contain FluidTankSlots.
 * Attaches the handler directly to the menu.
 * @see net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot
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

            var handler = ItemAccess.forPlayerCursor(getServerPlayer(), getServerPlayer().containerMenu)
                    .oneByOne()
                    .getCapability(Capabilities.Fluid.ITEM);
            if (handler != null) {

                var tank = getTank();
                if (tank == null) return;

                boolean isBucket = stack.getItem() instanceof BucketItem;
                if ((!isBucket && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                        || (isBucket && ((BucketItem) stack.getItem()).content == Fluids.EMPTY)) {
                    if (!canExtractFromTank(index)) return;

                    var genStack = tank.getStack(index);
                    if (genStack != null && genStack.what() != null) {
                        var fluid = ((AEFluidKey) genStack.what()).toStack((int) genStack.amount());

                        var extracted = Math.min(genStack.amount(), FluidType.BUCKET_VOLUME);

                        try (var tx = Transaction.openRoot()) {
                            int inserted = handler.insert(FluidResource.of(fluid), (int) extracted, tx);
                            if (inserted == 0) {
                                return;
                            }

                            var endAmount = genStack.amount() - inserted;
                            if (endAmount > 0) {
                                tank.setStack(index, new GenericStack(genStack.what(), genStack.amount() - inserted));
                            } else {
                                tank.setStack(index, null);
                            }
                            tx.commit();

                            if (inserted > 0) {
                                PacketDistributor.sendToPlayer(getServerPlayer(), new FluidTankClientAudioPacket(true));
                            }
                        }
                    }
                } else {
                    if (!canInsertInto(index)
                            || FluidUtil.getFirstStackContained(stack).isEmpty()) return;

                    var fluid = FluidUtil.getFirstStackContained(stack);
                    var genStack = GenericStack.fromFluidStack(fluid);
                    if (genStack != null && genStack.what() != null) {
                        try (var tx = Transaction.openRoot()) {
                            int extracted = handler.extract(FluidResource.of(fluid), FluidType.BUCKET_VOLUME, tx);
                            if (extracted == 0) {
                                return;
                            }

                            var inserted = tank.insert(index, genStack.what(), extracted, Actionable.MODULATE);
                            var toReturn = extracted - inserted;
                            if (toReturn > 0) {
                                if (handler.insert(FluidResource.of(fluid), (int) (extracted - inserted), tx)
                                        < toReturn) {
                                    return;
                                }
                            }

                            tx.commit();

                            PacketDistributor.sendToPlayer(getServerPlayer(), new FluidTankClientAudioPacket(true));
                        }
                    }
                }
            }
        }
    }
}
