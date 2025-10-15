package net.pedroksl.ae2addonlib.client.widgets;

import java.util.*;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.core.localization.LocalizationEnum;

public class ToolbarActionButton extends AddonIconButton {
    private static Map<IActionEnum, ButtonAppearance> appearances;
    private final IActionEnum action;

    public ToolbarActionButton(IActionEnum action, Runnable onPress) {
        this(action, a -> onPress.run());
    }

    public ToolbarActionButton(IActionEnum action, Consumer<IActionEnum> onPress) {
        super(btn -> onPress.accept(action));

        this.action = action;
        if (appearances == null) {
            appearances = new HashMap<>();

            registerApp(
                    Icons.DIRECTION_OUTPUT,
                    AddonActionItems.DIRECTIONAL_OUTPUT,
                    LibText.DirectionalOutput.text(),
                    LibText.DirectionalOutputHint.text());

            registerAppearances();
        }
    }

    protected void registerAppearances() {}

    protected static void registerApp(
            IBlitterIcon icon, IActionEnum action, LocalizationEnum title, LocalizationEnum hint) {
        registerApp(icon, action, title.text(), hint.text());
    }

    protected static void registerApp(IBlitterIcon icon, IActionEnum action, Component title, Component hint) {
        var lines = new ArrayList<Component>();
        lines.add(title);
        Collections.addAll(lines, hint);

        appearances.put(action, new ToolbarActionButton.ButtonAppearance(icon, null, lines));
    }

    public IActionEnum getAction() {
        return this.action;
    }

    @Nullable
    private ToolbarActionButton.ButtonAppearance getAppearance() {
        if (this.action != null) {
            return appearances.get(action);
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
    public List<Component> getTooltipMessage() {
        var app = getAppearance();
        if (app != null && app.tooltipLines() != null) {
            return app.tooltipLines();
        }
        return List.of();
    }

    private record ButtonAppearance(@Nullable IBlitterIcon icon, @Nullable Item item, List<Component> tooltipLines) {}
}
