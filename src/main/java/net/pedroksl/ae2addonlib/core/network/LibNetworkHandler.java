package net.pedroksl.ae2addonlib.core.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankClientAudioPacket;
import net.pedroksl.ae2addonlib.core.network.clientPacket.FluidTankStackUpdatePacket;
import net.pedroksl.ae2addonlib.core.network.clientPacket.OutputDirectionUpdatePacket;
import net.pedroksl.ae2addonlib.core.network.serverPacket.AddonConfigButtonPacket;
import net.pedroksl.ae2addonlib.core.network.serverPacket.AddonHotkeyPacket;
import net.pedroksl.ae2addonlib.core.network.serverPacket.FluidTankItemUsePacket;

/**
 * The lib's network handler. Registers the lib's packets.
 */
public class LibNetworkHandler extends NetworkHandler {

    /**
     * The handler's singleton instance.
     */
    public static final LibNetworkHandler INSTANCE = new LibNetworkHandler();

    LibNetworkHandler() {
        super(AE2AddonLib.MOD_ID);
    }

    @Override
    public void onRegister(PayloadRegistrar registrar) {
        clientbound(registrar, FluidTankClientAudioPacket.TYPE, FluidTankClientAudioPacket.STREAM_CODEC);
        clientbound(registrar, FluidTankStackUpdatePacket.TYPE, FluidTankStackUpdatePacket.STREAM_CODEC);
        clientbound(registrar, OutputDirectionUpdatePacket.TYPE, OutputDirectionUpdatePacket.STREAM_CODEC);

        serverbound(registrar, AddonConfigButtonPacket.TYPE, AddonConfigButtonPacket.STREAM_CODEC);
        serverbound(registrar, FluidTankItemUsePacket.TYPE, FluidTankItemUsePacket.STREAM_CODEC);
        serverbound(registrar, AddonHotkeyPacket.TYPE, AddonHotkeyPacket.STREAM_CODEC);
    }
}
