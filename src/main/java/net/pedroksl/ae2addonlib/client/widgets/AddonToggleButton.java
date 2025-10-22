package net.pedroksl.ae2addonlib.client.widgets;

import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;

import appeng.client.gui.widgets.ITooltip;

/**
 * Implementation of {@link appeng.client.gui.widgets.ToggleButton} that uses a {@link IBlitterIcon} instead of an {@link appeng.client.gui.Icon}.
 * This change opens the button to be used with custom textures.
 */
public class AddonToggleButton extends AddonIconButton implements ITooltip {

    private final Listener listener;

    private final IBlitterIcon iconOn;
    private final IBlitterIcon iconOff;

    private List<Component> tooltipOn = Collections.emptyList();
    private List<Component> tooltipOff = Collections.emptyList();

    private boolean state;

    /**
     * Constructs a toggle button with constant tooltips.
     * @param on Texture for an enabled button.
     * @param off Texture for a disabled button.
     * @param displayName Text for the title of the tooltip.
     * @param displayHint Text for the body of the tooltip.
     * @param listener Method ran to save changes.
     */
    public AddonToggleButton(
            IBlitterIcon on, IBlitterIcon off, Component displayName, Component displayHint, Listener listener) {
        this(on, off, listener);
        setTooltipOn(List.of(displayName, displayHint));
        setTooltipOff(List.of(displayName, displayHint));
    }

    /**
     * Constructs a toggle button with constant tooltips.
     * @param on Texture for an enabled button.
     * @param off Texture for a disabled button.
     * @param displayName Text for the title of the tooltip.
     * @param displayHintOn Text for the body of the tooltip on the enabled state.
     * @param displayHintOff Text for the body of the tooltip on the disabled state.
     * @param listener Method ran to save changes.
     */
    public AddonToggleButton(
            IBlitterIcon on,
            IBlitterIcon off,
            Component displayName,
            Component displayHintOn,
            Component displayHintOff,
            Listener listener) {
        this(on, off, listener);
        setTooltipOn(List.of(displayName, displayHintOn));
        setTooltipOff(List.of(displayName, displayHintOff));
    }

    /**
     * Constructs a toggle button without text components.
     * @param on Texture for an enabled button.
     * @param off Texture for a disabled button.
     * @param listener Method ran to save changes.
     */
    public AddonToggleButton(IBlitterIcon on, IBlitterIcon off, Listener listener) {
        super(null);
        this.iconOn = on;
        this.iconOff = off;
        this.listener = listener;
    }

    /**
     * Changes the tooltip body for the enabled state.
     * @param lines Text lines for the tooltip body.
     */
    public void setTooltipOn(List<Component> lines) {
        this.tooltipOn = lines;
    }

    /**
     * Changes the tooltip body for the disabled state.
     * @param lines Text lines for the tooltip body.
     */
    public void setTooltipOff(List<Component> lines) {
        this.tooltipOff = lines;
    }

    @Override
    public void onPress() {
        this.listener.onChange(!state);
    }

    /**
     * Sets the toggle button's state.
     * @param isOn The desired state.
     */
    public void setState(boolean isOn) {
        this.state = isOn;
    }

    /**
     * Getter for the button icon.
     * @return The icon.
     */
    protected IBlitterIcon getIcon() {
        return this.state ? this.iconOn : this.iconOff;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return state ? tooltipOn : tooltipOff;
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return super.isTooltipAreaVisible() && !getTooltipMessage().isEmpty();
    }

    /**
     * Interface used to wrap the change listener.
     */
    @FunctionalInterface
    public interface Listener {
        /**
         * The method called when applying changes.
         * @param state The current state.
         */
        void onChange(boolean state);
    }
}
