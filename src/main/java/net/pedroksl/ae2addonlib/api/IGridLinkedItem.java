package net.pedroksl.ae2addonlib.api;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.features.IGridLinkableHandler;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.core.localization.PlayerMessages;
import appeng.util.Platform;

/**
 * <p>Easily attachable {@link LinkableHandler} for grid connected items.</p>
 * To create a linkable item using this, make your item implement this class and register them after the common setup event.
 * This can be done by adding a listener to the {@link net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent} and calling
 * the {@link net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent#enqueueWork(Runnable)} method. From there, use
 * {@link appeng.api.features.GridLinkables#register(ItemLike, IGridLinkableHandler)} calls for each grid linked item,
 * using {@link #LINKABLE_HANDLER} as the second argument.
 */
public interface IGridLinkedItem {

    /**
     * The instance of an {@link IGridLinkableHandler}. Used during the registration process.
     */
    IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    /**
     * Helper function to retrieve the position of the node to which this item is linked from the data component.
     * @param item The relative item stack.
     * @return The global position of the linked grid. Null if unlinked.
     */
    default @Nullable GlobalPos getLinkedPosition(ItemStack item) {
        return item.get(AEComponents.WIRELESS_LINK_TARGET);
    }

    /**
     * Helper function to retrieve the grid to which this item is linked. <br>
     * Version of {@link #getLinkedGrid(ItemStack, Level, Consumer)} that ignores the errors.
     * @param stack The relative item stack.
     * @param level The level.
     * @return The linked grid. Null if unlinked.
     */
    default @Nullable IGrid getLinkedGrid(ItemStack stack, Level level) {
        return getLinkedGrid(stack, level, null);
    }

    /**
     * Helper function to retrieve the grid to which this item is linked.
     * @param stack The relative item stack.
     * @param level The level.
     * @param errorConsumer The function used to return erros if any.
     * @return The linked grid. Null if unlinked.
     */
    default @Nullable IGrid getLinkedGrid(ItemStack stack, Level level, @Nullable Consumer<Component> errorConsumer) {
        if (level instanceof ServerLevel serverLevel) {
            GlobalPos linkedPos = this.getLinkedPosition(stack);
            if (linkedPos == null) {
                if (errorConsumer != null) {
                    errorConsumer.accept(PlayerMessages.DeviceNotLinked.text());
                }

                return null;
            } else {
                ServerLevel linkedLevel = serverLevel.getServer().getLevel(linkedPos.dimension());
                if (linkedLevel == null) {
                    if (errorConsumer != null) {
                        errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                    }

                    return null;
                } else {
                    BlockEntity be = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
                    if (be instanceof IWirelessAccessPoint accessPoint) {
                        IGrid grid = accessPoint.getGrid();
                        if (grid == null && errorConsumer != null) {
                            errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                        }

                        return grid;
                    } else {
                        if (errorConsumer != null) {
                            errorConsumer.accept(PlayerMessages.LinkedNetworkNotFound.text());
                        }

                        return null;
                    }
                }
            }
        } else {
            return null;
        }
    }

    /**
     * Interface for implementers to add behavior after the linking process of the item.
     * @param itemStack The item stack being linked.
     * @param pos The global position being linked to.
     */
    default void onLink(ItemStack itemStack, GlobalPos pos) {}

    /**
     * Interface for implementers to add behavior before the unlinking process of the item.
     * @param itemStack The item stack being unlinked.
     */
    default void onUnlink(ItemStack itemStack) {}

    /**
     * Inner implementation of the {@link IGridLinkableHandler}.
     */
    final class LinkableHandler implements IGridLinkableHandler {
        public boolean canLink(ItemStack stack) {
            return stack.getItem() instanceof IGridLinkedItem;
        }

        public void link(ItemStack itemStack, GlobalPos pos) {
            itemStack.set(AEComponents.WIRELESS_LINK_TARGET, pos);
            if (itemStack.getItem() instanceof IGridLinkedItem item) {
                item.onLink(itemStack, pos);
            }
        }

        public void unlink(ItemStack itemStack) {
            if (itemStack.getItem() instanceof IGridLinkedItem item) {
                item.onUnlink(itemStack);
            }
            itemStack.remove(AEComponents.WIRELESS_LINK_TARGET);
        }
    }
}
