package net.pedroksl.ae2addonlib.client.widgets;

import java.util.*;
import java.util.function.Consumer;

import com.mojang.logging.LogUtils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.network.chat.Component;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.core.localization.LocalizationEnum;

/**
 * Actions buttons designed to be added to the left toolbar, but without being attached to a server setting.
 */
public class ToolbarActionButton extends AddonIconButton {
    private static final Logger LOG = LogUtils.getLogger();
    private static final Map<IActionEnum, ButtonAppearance> APPEARANCES = new HashMap<>();
    private final IActionEnum action;

    /**
     * Constructs an action button using an action's appearance and a custom runnable.
     * @param action The action to use for the button's appearance.
     * @param onPress The runnable to call when the button is pressed.
     */
    public ToolbarActionButton(IActionEnum action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    /**
     * Constructs an action button using an action's appearance and a custom runnable.
     * @param action The action to use for the button's appearance.
     * @param onPress The runnable to call when the button is pressed.
     */
    public ToolbarActionButton(IActionEnum action, Consumer<IActionEnum> onPress) {
        super(btn -> onPress.accept(action));

        this.action = action;

        if (!APPEARANCES.containsKey(action)) {
            ButtonAppearance app;
            if (action instanceof AddonActionItems) {
                app = getLibAppearance(action);
            } else {
                app = getAppearance(action);
            }

            if (app == null) {
                LOG.error("Tried to initialize ToolbarActionButton with an invalid action");
                throw new IllegalArgumentException();
            } else {
                APPEARANCES.put(action, app);
            }
        }
    }

    /**
     * Asks the inheritor for the appearance of an action. <br>
     * Appearances are cached after the first use.
     * @param action The action to get the appearance of.
     * @return The button appearance.
     */
    protected ButtonAppearance getAppearance(IActionEnum action) {
        return null;
    }

    /**
     * Asks the lib for the appearance of an action. <br>
     * Appearances are cached after the first use.
     * @param action The action to get the appearance of.
     * @return The button appearance.
     */
    private ButtonAppearance getLibAppearance(IActionEnum action) {
        return switch (action) {
            case AddonActionItems.DIRECTIONAL_OUTPUT -> createApp(
                    LibIcons.DIRECTION_OUTPUT, LibText.DirectionalOutput.text(), LibText.DirectionalOutputHint.text());
            default -> null;
        };
    }

    /**
     * Creates an appearance using texts directly from a {@link LocalizationEnum}.
     * @param icon The icon of the button.
     * @param title The enum entry that represents the title text for this button's tooltip.
     * @param hint The enum entry that represents the body text for this button's tooltip.
     * @return The button appearance.
     */
    protected ButtonAppearance createApp(IBlitterIcon icon, LocalizationEnum title, LocalizationEnum hint) {
        return createApp(icon, title.text(), hint.text());
    }

    /**
     * Creates an appearance using texts directly from a {@link Component}s.
     * @param icon The icon of the button.
     * @param title The title text for this button's tooltip.
     * @param hint The body text for this button's tooltip.
     * @return The button appearance.
     */
    protected ButtonAppearance createApp(IBlitterIcon icon, Component title, Component hint) {
        var lines = new ArrayList<Component>();
        lines.add(title);
        Collections.addAll(lines, hint);

        return new ButtonAppearance(icon, lines);
    }

    /**
     * Getter for tha action of this button.
     * @return The actions of this button.
     */
    public IActionEnum getAction() {
        return this.action;
    }

    @Override
    protected IBlitterIcon getIcon() {
        var app = APPEARANCES.get(this.action);
        if (app != null && app.icon != null) {
            return app.icon;
        }
        return LibIcons.TOOLBAR_BUTTON_BACKGROUND;
    }

    @Override
    public List<Component> getTooltipMessage() {
        var app = APPEARANCES.get(this.action);
        if (app != null && app.tooltipLines != null) {
            return app.tooltipLines();
        }
        return List.of();
    }

    /**
     * Container for the appearance of a button.
     * @param icon The button's icon.
     * @param tooltipLines The list of text components used to draw the button's tooltip.
     */
    protected record ButtonAppearance(@Nullable IBlitterIcon icon, List<Component> tooltipLines) {}
}
