package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.api.SettingsRegistry;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.config.Setting;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.AELog;
import appeng.menu.AEBaseMenu;
import appeng.util.EnumCycler;

/**
 * Class used to define the packet used to toggle/cycle between options in {@link SettingsRegistry}.
 * It is automatically sent when using an {@link net.pedroksl.ae2addonlib.client.widgets.AddonSettingToggleButton}.
 * This packet should only be used with settings registered by {@link SettingsRegistry}, otherwise it will throw.
 */
public class AddonConfigButtonPacket extends AddonPacket {

    private final String modId;
    private final Setting<?> option;
    private final boolean backwards;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public AddonConfigButtonPacket(FriendlyByteBuf stream) {
        this.modId = stream.readUtf();
        this.option = SettingsRegistry.getOrThrow(modId, stream.readUtf());
        this.backwards = stream.readBoolean();
    }

    /**
     * Constructs the packet to send to the stream.
     * @param modId The MOD_ID of the requesting mod.
     * @param option The setting to toggle/cycle
     * @param backwards Determines if the cycling rotation should be forwards or backwards.
     */
    public AddonConfigButtonPacket(String modId, Setting<?> option, boolean backwards) {
        this.modId = modId;
        this.option = option;
        this.backwards = backwards;
    }

    @Override
    protected void write(FriendlyByteBuf data) {
        data.writeUtf(modId);
        data.writeUtf(option.getName());
        data.writeBoolean(backwards);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (player.containerMenu instanceof AEBaseMenu baseMenu) {
            if (baseMenu.getTarget() instanceof IConfigurableObject configurableObject) {
                var cm = configurableObject.getConfigManager();
                if (cm.hasSetting(option)) {
                    cycleSetting(cm, option);
                } else {
                    AELog.info("Ignoring unsupported setting %s sent by client on %s", option, baseMenu.getTarget());
                }
            }
        }
    }

    private <T extends Enum<T>> void cycleSetting(IConfigManager cm, Setting<T> setting) {
        var currentValue = cm.getSetting(setting);
        var nextValue = EnumCycler.rotateEnum(currentValue, backwards, setting.getValues());
        cm.putSetting(setting, nextValue);
    }
}
