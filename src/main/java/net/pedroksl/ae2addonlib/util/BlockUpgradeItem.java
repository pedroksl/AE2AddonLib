package net.pedroksl.ae2addonlib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.items.AEBaseItem;

/**
 * <p>A base for any item that upgrades a block entity into another.</p>
 * Provies a helper method that takes in the entity pair and desired final {@link BlockState}.
 */
public class BlockUpgradeItem extends AEBaseItem {

    /**
     * Constructs the block upgrade item.
     * @param pProperties The item properties.
     */
    public BlockUpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * Helper method to replace a {@link BlockEntity} pair in world, keeping all previous block components.
     * @param world The level in which the replacement takes place.
     * @param pos The in-world block position of the target entity.
     * @param oldTile The old block entity.
     * @param newTile The new block entity.
     * @param newBlock The desired block state to initialize the new block entity.
     */
    protected void replaceTile(
            Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        CompoundTag contents = oldTile.serializeNBT();
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.deserializeNBT(contents);
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }
}
