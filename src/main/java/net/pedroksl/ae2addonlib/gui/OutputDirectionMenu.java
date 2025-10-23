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
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.network.clientPacket.OutputDirectionUpdatePacket;
import net.pedroksl.ae2addonlib.registry.helpers.LibMenus;

import appeng.api.orientation.RelativeSide;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;

/**
 * The menu for a {@link net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen}.
 * Used by block entities that implement the {@link IDirectionalOutputHost} interface.
 */
public class OutputDirectionMenu extends AEBaseMenu implements ISubMenu {

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.allOf(RelativeSide.class);

    private final IDirectionalOutputHost host;

    private static final String CLEAR = "clearSides";
    private static final String UPDATE_SIDES = "updateSides";

    /**
     * Default constructor called when AE2 first initializes menus before configuring the instance.
     * @param id The menu id.
     * @param ip The player's inventory.
     * @param host The menu's {@link IDirectionalOutputHost}.
     */
    public OutputDirectionMenu(int id, Inventory ip, IDirectionalOutputHost host) {
        this(LibMenus.OUTPUT_DIRECTION.get(), id, ip, host);
    }

    /**
     * Constructs the class an initializes the client actions.
     * @param type The menu's {@link MenuType}.
     * @param id The menu id.
     * @param ip The player's inventory.
     * @param host The menu's {@link IDirectionalOutputHost}.
     */
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

    /**
     * Open function provided to be called by parent to initialize the menu with some parameters.
     * @param player The server player.
     * @param locator The menu host locator.
     * @param allowedOutputs The initial value of the enabled/disabled outputs.
     */
    public static void open(ServerPlayer player, MenuLocator locator, EnumSet<RelativeSide> allowedOutputs) {
        MenuOpener.open(LibMenus.OUTPUT_DIRECTION.get(), player, locator);

        if (player.containerMenu instanceof OutputDirectionMenu cca) {
            cca.setAllowedOutputs(allowedOutputs);
            cca.broadcastChanges();
        }
    }

    /**
     * Reads the neighbor block entities and creates a matching {@link ItemStack} for the screen to render over the buttons.
     * @param side The relative side.
     * @return An item stack representing the neighbor block entity.
     */
    public ItemStack getAdjacentBlock(RelativeSide side) {
        var dir = host.getOrientation().getSide(side);
        BlockPos blockPos = host.getBlockPosition().relative(dir);

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
            LibNetworkHandler.INSTANCE.sendTo(
                    new OutputDirectionUpdatePacket(this.allowedOutputs), (ServerPlayer) getPlayer());
        }
    }

    /**
     * Gets the {@link Level} from the player inventory.
     * @return The level.
     */
    public Level getLevel() {
        return this.getPlayerInventory().player.level();
    }

    private void setAllowedOutputs(EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs.clone();
    }

    /**
     * Clears all enabled outputs. Called from the client's screen.
     */
    public void clearSides() {
        if (isClientSide()) {
            sendClientAction(CLEAR);
            return;
        }

        this.allowedOutputs.clear();
        this.getHost().updateOutputSides(this.allowedOutputs);
    }

    /**
     * Changes the state of one {@link RelativeSide}.
     * @param side The side to toggle.
     */
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
        LibNetworkHandler.INSTANCE.sendTo(
                new OutputDirectionUpdatePacket(this.allowedOutputs), (ServerPlayer) getPlayer());
    }
}
