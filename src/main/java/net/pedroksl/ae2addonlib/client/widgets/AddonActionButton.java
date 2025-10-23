package net.pedroksl.ae2addonlib.client.widgets;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.client.gui.Icon;

/**
 * Lib implementation of {@link appeng.client.gui.widgets.ActionButton }
 * This class provides an entry point for addons to register their appearances.
 */
public class AddonActionButton extends AddonIconButton {
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n", Pattern.LITERAL);
    /**
     * Member that holds the icon render for this button.
     */
    protected final IBlitterIcon icon;

    /**
     * Convenience constructor that will encapsulate a {@link Runnable} into a
     * {@link Consumer} for the default constructor. This version should be used
     * when only one action should be taken, despite the button's state.
     * @param action The enum entry for which the appearance is required.
     * @param onPress The action that will be taken when the button is pressed.
     */
    public AddonActionButton(IActionEnum action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    /**
     * Default constructor that takes the {@link IActionEnum} entry and a {@link Consumer}
     * that can read the {@link Button}'s state to take action.
     * @param action The enum entry for which the appearance is required.
     * @param onPress The action that will be taken when the button is pressed.
     */
    public AddonActionButton(IActionEnum action, Consumer<Button> onPress) {
        super(onPress::accept);

        Component displayName;
        Component displayValue;
        if (action instanceof AddonActionItems item) {
            switch (item) {
                case FLUID_FLUSH -> {
                    icon = LibIcons.CLEAR_SMALL;
                    displayName = LibText.ClearButton.text();
                    displayValue = LibText.ClearFluidButtonHint.text();
                }
                case CLEAR -> {
                    icon = LibIcons.CLEAR_SMALL;
                    displayName = LibText.ClearButton.text();
                    displayValue = LibText.ClearSidesButtonHint.text();
                }
                default -> throw new IllegalArgumentException("Unknown ActionItem: " + action);
            }
        } else {
            var app = getAppearance(action);
            if (app == null) {
                throw new IllegalArgumentException("Unknown ActionItem: " + action);
            }

            this.icon = app.icon;
            displayName = app.title;
            displayValue = app.hint;
        }

        setMessage(buildMessage(displayName, displayValue));
    }

    /**
     * This method is called when the button needs to decide what appearance to use.
     * It should be overridden when custom appearances are needed for the Addons {@link IActionEnum} implementation.
     * @param action  The enum entry for which the appearance is required.
     * @return The appearance that will be used for this button.
     */
    @Nullable
    protected Appearance getAppearance(IActionEnum action) {
        return null;
    }

    /**
     * @return The {@link Icon} of this button.
     */
    @Override
    protected IBlitterIcon getIcon() {
        return icon;
    }

    private Component buildMessage(Component displayName, @Nullable Component displayValue) {
        String name = displayName.getString();
        if (displayValue == null) {
            return Component.literal(name);
        }
        String value = displayValue.getString();

        value = PATTERN_NEW_LINE.matcher(value).replaceAll("\n");
        final StringBuilder sb = new StringBuilder(value);

        int i = sb.lastIndexOf("\n");
        if (i <= 0) {
            i = 0;
        }
        while (i + 30 < sb.length() && (i = sb.lastIndexOf(" ", i + 30)) != -1) {
            sb.replace(i, i + 1, "\n");
        }

        return Component.literal(name + '\n' + sb);
    }

    /**
     * Record used to hold the parameters of each appearance.
     * @param icon The {@link IBlitterIcon} entry for this button.
     * @param title The text that will be used as a title for the tooltip of this button.
     * @param hint The text remaining text for this button.
     */
    public record Appearance(IBlitterIcon icon, Component title, Component hint) {}
}
