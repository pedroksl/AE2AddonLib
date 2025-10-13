package net.pedroksl.ae2addonlib.client.widgets;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.pedroksl.ae2addonlib.registry.LibText;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

public class AddonActionButton extends IconButton {
    private static final Pattern PATTERN_NEW_LINE = Pattern.compile("\\n", Pattern.LITERAL);
    protected final Icon icon;

    public AddonActionButton(IActionEnum action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public AddonActionButton(IActionEnum action, Consumer<IActionEnum> onPress) {
        super(btn -> onPress.accept(action));

        Component displayName;
        Component displayValue;
        if (action instanceof AddonActionItems item) {
            switch (item) {
                case FLUID_FLUSH -> {
                    icon = Icon.S_CLEAR;
                    displayName = LibText.ClearButton.text();
                    displayValue = LibText.ClearFluidButtonHint.text();
                }
                case CLEAR -> {
                    icon = Icon.S_CLEAR;
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

    @Nullable
    protected Appearance getAppearance(IActionEnum action) {
        return null;
    }

    @Override
    protected Icon getIcon() {
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

    public record Appearance(Icon icon, Component title, Component hint) {}
}
