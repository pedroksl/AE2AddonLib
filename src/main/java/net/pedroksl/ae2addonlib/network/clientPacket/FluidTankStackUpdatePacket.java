package net.pedroksl.ae2addonlib.network.clientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

public record FluidTankStackUpdatePacket(int index, FluidStack stack) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidTankStackUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    FluidTankStackUpdatePacket::index,
                    FluidStack.OPTIONAL_STREAM_CODEC,
                    FluidTankStackUpdatePacket::stack,
                    FluidTankStackUpdatePacket::new);

    public static final Type<FluidTankStackUpdatePacket> TYPE =
            CustomAppEngPayload.createType("ae2lib_fluid_tank_stack_update");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.updateFluidTankContents(index, stack);
        }
    }
}
