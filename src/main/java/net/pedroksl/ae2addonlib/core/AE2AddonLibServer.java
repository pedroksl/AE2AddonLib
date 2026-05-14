package net.pedroksl.ae2addonlib.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = AE2AddonLib.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class AE2AddonLibServer extends AE2AddonLib {
    public AE2AddonLibServer(IEventBus modEventBus, ModContainer container) {
        super(modEventBus, container);
    }
}
