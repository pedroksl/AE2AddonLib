package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.api.SettingsRegistry;

import appeng.api.config.Setting;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.AELog;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;
import appeng.menu.AEBaseMenu;
import appeng.util.EnumCycler;

/**
 * Record used to define the packet used to toggle/cycle between options in {@link SettingsRegistry}.
 * It is automatically sent when using an {@link net.pedroksl.ae2addonlib.client.widgets.AddonSettingToggleButton}.
 * This packet should only be used with settings registered by {@link SettingsRegistry}, otherwise it will throw.
 * @param modId The MOD_ID of the requesting mod.
 * @param option The setting to toggle/cycle
 * @param backwards Determines if the cycling rotation should be forwards or backwards.
 */
public record AddonConfigButtonPacket(String modId, Setting<?> option, boolean backwards) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AddonConfigButtonPacket> STREAM_CODEC =
            StreamCodec.ofMember(AddonConfigButtonPacket::write, AddonConfigButtonPacket::decode);

    public static final Type<AddonConfigButtonPacket> TYPE = CustomAppEngPayload.createType("ae2lib_config_button");

    @Override
    public Type<AddonConfigButtonPacket> type() {
        return TYPE;
    }

    private static AddonConfigButtonPacket decode(RegistryFriendlyByteBuf stream) {
        var modId = stream.readUtf();
        var option = SettingsRegistry.getOrThrow(modId, stream.readUtf());
        var rotationDirection = stream.readBoolean();
        return new AddonConfigButtonPacket(modId, option, rotationDirection);
    }

    private void write(RegistryFriendlyByteBuf data) {
        data.writeUtf(modId);
        data.writeUtf(option.getName());
        data.writeBoolean(backwards);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
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
