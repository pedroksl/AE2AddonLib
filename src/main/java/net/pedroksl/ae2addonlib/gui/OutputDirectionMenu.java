package net.pedroksl.ae2addonlib.gui;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;
import net.pedroksl.ae2addonlib.network.clientPacket.OutputDirectionUpdatePacket;
import net.pedroksl.ae2addonlib.registry.helpers.AddonMenus;

import appeng.api.orientation.RelativeSide;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;

public class OutputDirectionMenu extends AEBaseMenu implements ISubMenu {

    public EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    private final IDirectionalOutputHost host;

    private static final String CLEAR = "clearSides";
    private static final String UPDATE_SIDES = "updateSides";

    public OutputDirectionMenu(int id, Inventory ip, IDirectionalOutputHost host) {
        this(AddonMenus.OUTPUT_DIRECTION.get(), id, ip, host);
    }

    protected OutputDirectionMenu(
            MenuType<? extends OutputDirectionMenu> type, int id, Inventory ip, IDirectionalOutputHost host) {
        super(type, id, ip, host);
        this.host = host;

        registerClientAction(CLEAR, this::clearSides);
        registerClientAction(UPDATE_SIDES, RelativeSide.class, this::updateSideStatus);
    }

    @Override
    public IDirectionalOutputHost getHost() {
        return this.host;
    }

    public static void open(ServerPlayer player, MenuHostLocator locator, EnumSet<RelativeSide> allowedOutputs) {
        MenuOpener.open(AddonMenus.OUTPUT_DIRECTION.get(), player, locator);

        if (player.containerMenu instanceof OutputDirectionMenu cca) {
            cca.setAllowedOutputs(allowedOutputs);
            cca.broadcastChanges();
        }
    }

    public ItemStack getAdjacentBlock(RelativeSide side) {
        var dir = host.getOrientation().getSide(side);
        BlockPos blockPos = host.getBlockPos().relative(dir);

        Level level = getLevel();
        if (level == null) {
            return null;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.isAir()) {
            return blockState.getCloneItemStack(
                    new BlockHitResult(
                            blockPos.getCenter().relative(dir.getOpposite(), 0.5), dir.getOpposite(), blockPos, false),
                    level,
                    blockPos,
                    this.getPlayerInventory().player);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (isServerSide()) {
            PacketDistributor.sendToPlayer(
                    (ServerPlayer) getPlayer(), new OutputDirectionUpdatePacket(this.allowedOutputs));
        }
    }

    public Level getLevel() {
        return this.getPlayerInventory().player.level();
    }

    private void setAllowedOutputs(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs.clone();
    }

    public void clearSides() {
        if (isClientSide()) {
            sendClientAction(CLEAR);
            return;
        }

        this.allowedOutputs.clear();
        this.getHost().updateOutputSides(this.allowedOutputs);
    }

    public void updateSideStatus(RelativeSide side) {
        if (isClientSide()) {
            sendClientAction(UPDATE_SIDES, side);
            return;
        }

        if (this.allowedOutputs.contains(side)) {
            this.allowedOutputs.remove(side);
        } else {
            this.allowedOutputs.add(side);
        }

        this.getHost().updateOutputSides(allowedOutputs);
        PacketDistributor.sendToPlayer(
                (ServerPlayer) getPlayer(), new OutputDirectionUpdatePacket(this.allowedOutputs));
    }
}
