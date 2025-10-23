package net.pedroksl.ae2addonlib.client.widgets;

import java.util.*;
import java.util.function.Predicate;

import com.mojang.logging.LogUtils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.ae2addonlib.network.AddonPacket;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonConfigButtonPacket;

import appeng.api.config.Setting;
import appeng.client.gui.AEBaseScreen;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.LocalizationEnum;
import appeng.util.EnumCycler;

/**
 * Addon version of {@link appeng.client.gui.widgets.SettingToggleButton}. <br>
 * This implementation will accept addon appearances linked to addon settings.
 * These appearances can be registered by overriding {@link #registerAppearances}.
 * To create a version of {@link appeng.client.gui.widgets.ServerSettingToggleButton} that automatically sends
 * packets to the server to toggle/cycle settings, call {@link #serverButton}.
 * @param <T> Class that extends enum.
 */
public class AddonSettingToggleButton<T extends Enum<T>> extends AddonIconButton {
    private static final Logger LOG = LogUtils.getLogger();
    private static final Map<EnumPair<?>, ButtonAppearance> APPEARANCES = new HashMap<>();
    private final Setting<T> buttonSetting;
    private final IHandler<AddonSettingToggleButton<T>> onPress;
    private final EnumSet<T> validValues;
    private T currentValue;

    /**
     * Wrapper for a function that handles the effect of button clicks. Receives the pressed button as well as a boolean
     * that is true if the setting should cycle backwards.
     * @param <T> The button class.
     */
    @FunctionalInterface
    public interface IHandler<T extends AddonSettingToggleButton<?>> {
        /**
         * Function to handle button clicks.
         * @param button The button's instance.
         * @param backwards True if the cycling should be done backwards.
         */
        void handle(T button, boolean backwards);
    }

    /**
     * Constructs a button that accepts all values of a {@link Setting} as valid.
     * @param setting The setting to toggle/cycle.
     * @param val The initial value of the setting.
     * @param onPress The function to execute on button press.
     */
    public AddonSettingToggleButton(Setting<T> setting, T val, IHandler<AddonSettingToggleButton<T>> onPress) {
        this(setting, val, t -> true, onPress);
    }

    /**
     * Constructs a button that accepts not all values of a {@link Setting} as valid.
     * @param setting The setting to toggle/cycle.
     * @param val The initial value of the setting.
     * @param isValidValue A predicate used to determine the value settings.
     * @param onPress The function to execute on button press.
     */
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

        var key = new EnumPair<>(setting, val);
        if (!APPEARANCES.containsKey(key)) {
            registerAppearances();

            if (!APPEARANCES.containsKey(key)) {
                LOG.error("Tried to initialize button with invalid setting: {}", setting.getName());
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Creates an AddonSettingToggleButton that automatically sends a packet to the server to toggle/cycle settings on button press.
     * To use this with inheritor appearances, a version of this should be created that sends the mod id and the button's
     * constructor as the third and fourth parameter, respectively.
     * @param setting The setting to toggle/cycle.
     * @param val The initial value of the setting.
     * @param modId The MOD_ID of the inheritor.
     * @param factory The constructor for the inheritor's class.
     * @param <T> Class that extends enum.
     * @param <M> Class that extends this.
     * @return An instance of the inheritor's class.
     */
    public static <T extends Enum<T>, M extends AddonSettingToggleButton<T>> M serverButton(
            Setting<T> setting, T val, String modId, ServerSettingToggleFactory<M, T> factory) {
        return factory.create(
                setting,
                val,
                t -> true,
                (button, backwards) -> AddonSettingToggleButton.sendToServer(button, backwards, modId));
    }

    /**
     * Entry point for appearance registration
     * Insert {@link #registerApp} calls for
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

    /**
     * Registers an appearance using an icon and a list of text lines.
     * @param icon The icon of the button.
     * @param setting The setting of this appearance.
     * @param val The value of this appearance.
     * @param title The enum entry that represents the title text for this button's tooltip.
     * @param tooltipLines A list of components that represent the body text for this button's tooltip.
     * @param <T> Class that extends enum.
     */
    protected static <T extends Enum<T>> void registerApp(
            IBlitterIcon icon, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        APPEARANCES.put(new EnumPair<>(setting, val), new ButtonAppearance(icon, null, lines));
    }

    /**
     * Registers an appearance that renders an item instead of an icon.
     * @param item An item to be rendered in the button.
     * @param setting The setting of this appearance.
     * @param val The value of this appearance.
     * @param title The enum entry that represents the title text for this button's tooltip.
     * @param tooltipLines The enum entry that represents the body text for this button's tooltip.
     * @param <T> Class that extends enum.
     */
    protected static <T extends Enum<T>> void registerApp(
            ItemLike item, Setting<T> setting, T val, LocalizationEnum title, Component... tooltipLines) {
        var lines = new ArrayList<Component>();
        lines.add(title.text());
        Collections.addAll(lines, tooltipLines);

        APPEARANCES.put(new EnumPair<>(setting, val), new ButtonAppearance(null, item.asItem(), lines));
    }

    /**
     * Registers an appearance using an icon and {@link LocalizationEnum} entries for text.
     * @param icon The icon of the button.
     * @param setting The setting of this appearance.
     * @param val The value of this appearance.
     * @param title The enum entry that represents the title text for this button's tooltip.
     * @param hint The enum entry that represents the body text for this button's tooltip.
     * @param <T> Class that extends enum.
     */
    protected static <T extends Enum<T>> void registerApp(
            IBlitterIcon icon, Setting<T> setting, T val, LocalizationEnum title, LocalizationEnum hint) {
        registerApp(icon, setting, val, title, hint.text());
    }

    @Nullable
    private AddonSettingToggleButton.ButtonAppearance getAppearance() {
        if (this.buttonSetting != null && this.currentValue != null) {
            return APPEARANCES.get(new EnumPair<>(this.buttonSetting, this.currentValue));
        }
        return null;
    }

    @Override
    protected IBlitterIcon getIcon() {
        var app = getAppearance();
        if (app != null && app.icon != null) {
            return app.icon;
        }
        return LibIcons.TOOLBAR_BUTTON_BACKGROUND;
    }

    @Override
    protected Item getItemOverlay() {
        var app = getAppearance();
        if (app != null && app.item != null) {
            return app.item;
        }
        return null;
    }

    /**
     * Getter for the button's setting.
     * @return The button's setting.
     */
    public Setting<T> getSetting() {
        return this.buttonSetting;
    }

    /**
     * Getter for the current value of the button's setting.
     * @return The current value.
     */
    public T getCurrentValue() {
        return this.currentValue;
    }

    /**
     * Setter for the value of the button's setting.
     * @param e The value to set.
     */
    public void set(T e) {
        if (this.currentValue != e) {
            this.currentValue = e;
        }
    }

    /**
     * Peek at the next value of the button's setting without changing it.
     * @param backwards Determines the direction of the peek. True for backwards.
     * @return The peeked value.
     */
    public T getNextValue(boolean backwards) {
        return EnumCycler.rotateEnum(currentValue, backwards, validValues);
    }

    @Override
    public List<Component> getTooltipMessage() {

        if (this.buttonSetting == null || this.currentValue == null) {
            return Collections.emptyList();
        }

        var buttonAppearance = APPEARANCES.get(new EnumPair<>(this.buttonSetting, this.currentValue));
        if (buttonAppearance == null) {
            return Collections.singletonList(ButtonToolTips.NoSuchMessage.text());
        }

        return buttonAppearance.tooltipLines;
    }

    /**
     * Container class for a pair of Setting/Value. Implements hash and equals operators to be used as a map key.
     * @param <T> Class that extends enum.
     */
    protected static final class EnumPair<T extends Enum<T>> {

        final Setting<T> setting;
        final T value;

        /**
         * Class default constructor.
         * @param setting Contained setting.
         * @param value Contained value.
         */
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

    private static <T extends Enum<T>> void sendToServer(
            AddonSettingToggleButton<T> button, boolean backwards, String modId) {
        AddonPacket message = new AddonConfigButtonPacket(modId, button.getSetting(), backwards);
        LibNetworkHandler.INSTANCE.sendToServer(message);
    }

    /**
     * Container records for a button appearance.
     * @param icon The appearance's icon.
     * @param item The appearance's item.
     * @param tooltipLines The list of text components for the tooltip.
     */
    protected record ButtonAppearance(@Nullable IBlitterIcon icon, @Nullable Item item, List<Component> tooltipLines) {}

    /**
     * Wrapper for a button constructor. Used to create instances of inheritor's linked to this class's {@link #sendToServer}.
     * @param <C> Class that extends this.
     * @param <T> Class that extends enum.
     */
    @FunctionalInterface
    public interface ServerSettingToggleFactory<C, T extends Enum<T>> {
        /**
         * The inheritor's constructor.
         * @param setting The button's setting.
         * @param val The setting's initial value.
         * @param isValidValue The predicate to filter the available settings when cycling.
         * @param onPress The function to run when the button is pressed.
         * @return The button's new instance.
         */
        C create(Setting<T> setting, T val, Predicate<T> isValidValue, IHandler<AddonSettingToggleButton<T>> onPress);
    }
}
