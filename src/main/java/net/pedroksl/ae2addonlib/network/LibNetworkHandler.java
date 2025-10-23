package net.pedroksl.ae2addonlib.network;

import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.network.clientPacket.FluidTankStackUpdatePacket;
import net.pedroksl.ae2addonlib.network.clientPacket.OutputDirectionUpdatePacket;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonConfigButtonPacket;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonHotkeyPacket;
import net.pedroksl.ae2addonlib.network.serverPacket.FluidTankItemUsePacket;

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
    public void init() {
        // registerPacket(FluidTankClientAudioPacket.class, FluidTankClientAudioPacket::new);
        registerPacket(FluidTankStackUpdatePacket.class, FluidTankStackUpdatePacket::new);
        registerPacket(OutputDirectionUpdatePacket.class, OutputDirectionUpdatePacket::new);

        registerPacket(AddonConfigButtonPacket.class, AddonConfigButtonPacket::new);
        registerPacket(FluidTankItemUsePacket.class, FluidTankItemUsePacket::new);
        registerPacket(AddonHotkeyPacket.class, AddonHotkeyPacket::new);
    }
}
