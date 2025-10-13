package net.pedroksl.ae2addonlib.client.widgets;

import java.util.*;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonConfigButtonPacket;

import appeng.api.config.Setting;
import appeng.client.gui.AEBaseScreen;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.LocalizationEnum;
import appeng.core.network.ServerboundPacket;
import appeng.util.EnumCycler;

public class AddonSettingToggleButton<T extends Enum<T>> extends AddonIconButton {
    private static Map<EnumPair<?>, ButtonAppearance> appearances;
    private final Setting<T> buttonSetting;
    private final IHandler<AddonSettingToggleButton<T>> onPress;
    private final EnumSet<T> validValues;
    private T currentValue;

    @FunctionalInterface
    public interface IHandler<T extends AddonSettingToggleButton<?>> {
        void handle(T button, boolean backwards);
    }

    public AddonSettingToggleButton(Setting<T> setting, T val) {
        this(setting, val, t -> true, AddonSettingToggleButton::sendToServer);
    }

    public AddonSettingToggleButton(Setting<T> setting, T val, IHandler<AddonSettingToggleButton<T>> onPress) {
        this(setting, val, t -> true, onPress);
    }

    public AddonSettingToggleButton(
            Setting<T> setting, T val, Predicate<T> isValidValue, IHandler<AddonSettingToggleButton<T>> onPress) {
        super(AddonSettingToggleButton::onPress);
        this.onPress = onPress;

        // Build a list of values (in order) that are valid w.r.t. the given predicate
        EnumSet<T> validValues = EnumSet.allOf(val.getDeclaringClass());
        validValues.removeIf(isValidValue.negate());
        validValues.removeIf(s -> !setting.getValues().contains(s));
        this.validValues = validValues;

        this.buttonSetting = setting;
        this.currentValue = val;

        if (appearances == null) {
            appearances = new HashMap<>();

            registerAppearances();
        }
    }

    /**
     * Entry point for appearance registration
     * Insert AddonSettingToggleButton#registerApp calls for
     * every combination of Setting-Value pair.
     */
    protected void registerAppearances() {}

    private static void onPress(Button btn) {
        if (btn instanceof AddonSettingToggleButton) {
            ((AddonSettingToggleButton<?>) btn).triggerPress();
        }
    }

    private void triggerPress() {
        boolean backwards = false;
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof AEBaseScreen) {
            backwards = ((AEBaseScreen<?>) currentScreen).isHandlingRightClick();
        }
        onPress.handle(this, backwards);
    }

    protected static <T extends Enum<T>> void registerApp(
            IBlitterIcon icon, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        appearances.put(new EnumPair<>(setting, val), new ButtonAppearance(icon, null, lines));
    }

    protected static <T extends Enum<T>> void registerApp(
            ItemLike item, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        appearances.put(new EnumPair<>(setting, val), new ButtonAppearance(null, item.asItem(), lines));
    }

    protected static <T extends Enum<T>> void registerApp(
            IBlitterIcon icon, Setting<T> setting, T val, LocalizationEnum title, LocalizationEnum hint) {
        registerApp(icon, setting, val, title, hint.text());
    }

    @Nullable
    private AddonSettingToggleButton.ButtonAppearance getAppearance() {
        if (this.buttonSetting != null && this.currentValue != null) {
            return appearances.get(new EnumPair<>(this.buttonSetting, this.currentValue));
        }
        return null;
    }

    @Override
    protected IBlitterIcon getIcon() {
        var app = getAppearance();
        if (app != null && app.icon != null) {
            return app.icon;
        }
        return Icons.TOOLBAR_BUTTON_BACKGROUND;
    }

    @Override
    protected Item getItemOverlay() {
        var app = getAppearance();
        if (app != null && app.item != null) {
            return app.item;
        }
        return null;
    }

    public Setting<T> getSetting() {
        return this.buttonSetting;
    }

    public T getCurrentValue() {
        return this.currentValue;
    }

    public void set(T e) {
        if (this.currentValue != e) {
            this.currentValue = e;
        }
    }

    public T getNextValue(boolean backwards) {
        return EnumCycler.rotateEnum(currentValue, backwards, validValues);
    }

    @Override
    public List<Component> getTooltipMessage() {

        if (this.buttonSetting == null || this.currentValue == null) {
            return Collections.emptyList();
        }

        var buttonAppearance = appearances.get(new EnumPair<>(this.buttonSetting, this.currentValue));
        if (buttonAppearance == null) {
            return Collections.singletonList(ButtonToolTips.NoSuchMessage.text());
        }

        return buttonAppearance.tooltipLines;
    }

    private static final class EnumPair<T extends Enum<T>> {

        final Setting<T> setting;
        final T value;

        public EnumPair(Setting<T> setting, T value) {
            this.setting = setting;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return this.setting.hashCode() ^ this.value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final EnumPair<?> other = (EnumPair<?>) obj;
            return other.setting == this.setting && other.value == this.value;
        }
    }

    private static <T extends Enum<T>> void sendToServer(AddonSettingToggleButton<T> button, boolean backwards) {
        ServerboundPacket message = new AddonConfigButtonPacket(button.getSetting(), backwards);
        PacketDistributor.sendToServer(message);
    }

    private record ButtonAppearance(@Nullable IBlitterIcon icon, @Nullable Item item, List<Component> tooltipLines) {}
}
