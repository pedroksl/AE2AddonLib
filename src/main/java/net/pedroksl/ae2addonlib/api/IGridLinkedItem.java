package net.pedroksl.ae2addonlib.api;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import appeng.api.features.IGridLinkableHandler;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.core.localization.PlayerMessages;
import appeng.util.Platform;

/**
 * <p>Easily attachable {@link LinkableHandler} for grid connected items.</p>
 * To create a linkable item using this, make your item implement this class and register them after the common setup event.
 * This can be done by adding a listener to the {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent} and calling
 * the {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent#enqueueWork(Runnable)} method. From there, use
 * {@link appeng.api.features.GridLinkables#register(ItemLike, IGridLinkableHandler)} calls for each grid linked item,
 * using {@link #LINKABLE_HANDLER} as the second argument.
 */
public interface IGridLinkedItem {

    /**
     * Logger instance.
     */
    Logger LOG = LoggerFactory.getLogger(IGridLinkedItem.class);

    /**
     * The instance of an {@link IGridLinkableHandler}. Used during the registration process.
     */
    IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    /**
     * Tag used to read/write linked global position.
     */
    String TAG_ACCESS_POINT_POS = "accessPoint";

    /**
     * Helper function to retrieve the position of the node to which this item is linked from the data component.
     * @param item The relative item stack.
     * @return The global position of the linked grid. Null if unlinked.
     */
    default @Nullable GlobalPos getLinkedPosition(ItemStack item) {
        CompoundTag tag = item.getTag();
        if (tag != null && tag.contains(TAG_ACCESS_POINT_POS, Tag.TAG_COMPOUND)) {
            return GlobalPos.CODEC
                    .decode(NbtOps.INSTANCE, tag.get(TAG_ACCESS_POINT_POS))
                    .resultOrPartial(Util.prefix("Linked position", LOG::error))
                    .map(Pair::getFirst)
                    .orElse(null);
        } else {
            return null;
        }
    }

    /**
     * Helper function to retrieve the grid to which this item is linked. <br>
     * Version of {@link #getLinkedGrid(ItemStack, Level, Player)} that ignores the errors.
     * @param stack The relative item stack.
     * @param level The level.
     * @return The linked grid. Null if unlinked.
     */
    default @Nullable IGrid getLinkedGrid(ItemStack stack, Level level) {
        return getLinkedGrid(stack, level, null);
    }

    /**
     * Helper function to retrieve the grid to which this item is linked.
     * @param item The relative item stack.
     * @param level The level.
     * @param sendMessagesTo The player to send messages to.
     * @return The linked grid. Null if unlinked.
     */
    default IGrid getLinkedGrid(ItemStack item, Level level, @Nullable Player sendMessagesTo) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        var linkedPos = getLinkedPosition(item);
        if (linkedPos == null) {
            if (sendMessagesTo != null) {
                sendMessagesTo.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
            }
            return null;
        }

        var linkedLevel = serverLevel.getServer().getLevel(linkedPos.dimension());
        if (linkedLevel == null) {
            if (sendMessagesTo != null) {
                sendMessagesTo.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
            }
            return null;
        }

        var be = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
        if (!(be instanceof IWirelessAccessPoint accessPoint)) {
            if (sendMessagesTo != null) {
                sendMessagesTo.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
            }
            return null;
        }

        var grid = accessPoint.getGrid();
        if (grid == null) {
            if (sendMessagesTo != null) {
                sendMessagesTo.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
            }
        }
        return grid;
    }

    /**
     * Inner implementation of the {@link IGridLinkableHandler}.
     */
    final class LinkableHandler implements IGridLinkableHandler {
        public boolean canLink(ItemStack stack) {
            return stack.getItem() instanceof IGridLinkedItem;
        }

        public void link(ItemStack itemStack, GlobalPos pos) {
            GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result().ifPresent(tag -> itemStack
                    .getOrCreateTag()
                    .put(TAG_ACCESS_POINT_POS, tag));
        }

        public void unlink(ItemStack itemStack) {
            itemStack.removeTagKey(TAG_ACCESS_POINT_POS);
        }
    }
}
