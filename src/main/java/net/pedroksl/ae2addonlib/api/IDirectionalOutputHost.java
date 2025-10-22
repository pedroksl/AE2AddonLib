package net.pedroksl.ae2addonlib.api;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.storage.ISubMenuHost;

/**
 * Interface to be implemented by block entities that have directional output capabilities.
 * This host is necessary for the creation of a {@link net.pedroksl.ae2addonlib.gui.OutputDirectionMenu}.
 */
public interface IDirectionalOutputHost extends ISubMenuHost {

    /**
     * Getter for the block orientation.
     * @return The block orientation.
     */
    BlockOrientation getOrientation();

    /**
     * Getter for the block position.
     * @return The block position.
     */
    BlockPos getBlockPos();

    /**
     * Getter for the current state of the allowed outputs.
     * @return A set containing the enabled outputs.
     */
    EnumSet<RelativeSide> getAllowedOutputs();

    /**
     * Updates the output sides back to the block entity.
     * @param sides A set containing the enabled outputs.
     */
    void updateOutputSides(EnumSet<RelativeSide> sides);
}
