package net.pedroksl.ae2addonlib.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.pedroksl.ae2addonlib.AE2AddonLib;

/**
 * <p>Simple implementation of a water-based fluid.</p>
 * This class provides the basic components to render a fluid that uses the same textures as water.
 * The class can be extended to be customized by adding a tint and changing fluid properties.
 */
public class WaterBasedFluidType extends FluidType implements IClientFluidTypeExtensions {

    private final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
    private final ResourceLocation WATER_STILL = AE2AddonLib.makeId("block/water_still");
    private final ResourceLocation WATER_FLOW = AE2AddonLib.makeId("block/water_flowing");
    private final ResourceLocation WATER_OVERLAY = AE2AddonLib.makeId("block/water_overlay");

    /**
     * The color used to tint the fluid.
     */
    protected int tintColor = -1;

    /**
     * Constructs a water-based fluid with given properties.
     * @param properties The properties to use in fluid construction.
     */
    public WaterBasedFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ResourceLocation getStillTexture() {
        return WATER_STILL;
    }

    @Override
    public @NotNull ResourceLocation getFlowingTexture() {
        return WATER_FLOW;
    }

    @Override
    public ResourceLocation getOverlayTexture() {
        return WATER_OVERLAY;
    }

    @Override
    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return UNDERWATER_LOCATION;
    }

    @Override
    public int getTintColor() {
        return tintColor;
    }

    @Override
    public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return tintColor;
    }
}
