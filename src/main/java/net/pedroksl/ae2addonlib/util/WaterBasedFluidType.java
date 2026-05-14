package net.pedroksl.ae2addonlib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;

/**
 * <p>Simple implementation of a water-based fluid.</p>
 * This class provides the basic components to render a fluid that uses the same textures as water.
 * The class can be extended to be customized by adding a tint and changing fluid properties.
 */
public class WaterBasedFluidType extends FluidType implements IClientFluidTypeExtensions {

    private final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");
    private final Material WATER_STILL = new Material(AE2AddonLib.makeId("block/water_still"));
    private final Material WATER_FLOW = new Material(AE2AddonLib.makeId("block/water_flowing"));
    private final Material WATER_OVERLAY = new Material(AE2AddonLib.makeId("block/water_overlay"));

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
    public Identifier getRenderOverlayTexture(Minecraft mc) {
        return UNDERWATER_LOCATION;
    }

    public FluidModel.Unbaked getFluidModel() {
        return new FluidModel.Unbaked(WATER_STILL, WATER_FLOW, WATER_OVERLAY, (_) -> tintColor);
    }
}
