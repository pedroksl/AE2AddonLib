package net.pedroksl.ae2addonlib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankClientAudioPacket;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankStackUpdatePacket;
import net.pedroksl.ae2addonlib.core.network.clientPacket.OutputDirectionUpdatePacket;

public class LibClientNetworkHandler extends ClientNetworkHandler {

    @Override
    public void registerPackets(RegisterClientPayloadHandlersEvent event) {
        register(event, FluidTankClientAudioPacket.TYPE, this::handleFluidTankClientAudioPacket);
        register(event, FluidTankStackUpdatePacket.TYPE, this::handleFluidTanStackUpdatePacket);
        register(event, OutputDirectionUpdatePacket.TYPE, this::handleOutputDirectionUpdatePacket);
    }

    public void handleFluidTankClientAudioPacket(
            FluidTankClientAudioPacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            IFluidTankScreen.playDownSound(packet.isInsert());
        }
    }

    public void handleFluidTanStackUpdatePacket(FluidTankStackUpdatePacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof IFluidTankScreen screen) {
            screen.updateFluidTankContents(packet.index(), packet.stack());
        }
    }

    public void handleOutputDirectionUpdatePacket(
            OutputDirectionUpdatePacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof OutputDirectionScreen screen) {
            screen.update(packet.sides());
        }
    }
}
