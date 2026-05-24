package net.pedroksl.ae2addonlib.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;
import net.pedroksl.ae2addonlib.util.WaterBasedFluidType;

public class WaterBasedFluidModel<T extends WaterBasedFluidType> implements IClientFluidTypeExtensions {

    private final T type;

    private final Identifier UNDERWATER_LOCATION = Identifier.withDefaultNamespace("textures/misc/underwater.png");
    private final Material WATER_STILL = new Material(AE2AddonLib.makeId("block/water_still"));
    private final Material WATER_FLOW = new Material(AE2AddonLib.makeId("block/water_flowing"));
    private final Material WATER_OVERLAY = new Material(AE2AddonLib.makeId("block/water_overlay"));

    public WaterBasedFluidModel(T type) {
        this.type = type;
    }

    public FluidModel.Unbaked get() {
        return new FluidModel.Unbaked(WATER_STILL, WATER_FLOW, WATER_OVERLAY, (_) -> this.type.getTintColor());
    }

    @Override
    public Identifier getRenderOverlayTexture(Minecraft mc) {
        return UNDERWATER_LOCATION;
    }
}
