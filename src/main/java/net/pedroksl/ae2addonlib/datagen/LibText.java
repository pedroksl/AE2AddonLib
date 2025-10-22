package net.pedroksl.ae2addonlib.datagen;

import net.pedroksl.ae2addonlib.AE2AddonLib;

import guideme.internal.data.LocalizationEnum;

/**
 * Lib's {@link LocalizationEnum}, containing text's for the lib's screens and widgets.
 */
public enum LibText implements LocalizationEnum {
    /**
     * Text entry for clear buttons.
     */
    ClearButton("Clear", Type.TOOLTIP),
    /**
     * Text entry for fluid flushing buttons.
     */
    ClearFluidButtonHint("Flush the remaining fluid from the machine.", Type.TOOLTIP),
    /**
     * Text entry for allowed output clearing buttons.
     */
    ClearSidesButtonHint("Disable output from all sides of the machine.", Type.TOOLTIP),
    /**
     * Text entry for the title of the tooltip of toolbar button to open directional output configuration.
     */
    DirectionalOutput("Directional Output", Type.TOOLTIP),
    /**
     * Text entry for the body text of the tooltip of toolbar button to open directional output configuration.
     */
    DirectionalOutputHint("Configure which directions are allowed for output auto-export", Type.TOOLTIP),
    /**
     * Text entry for enabled tooltips.
     */
    Enabled("Enabled", Type.TOOLTIP),
    /**
     * Text entry for disabled tooltips.
     */
    Disabled("Disabled", Type.TOOLTIP),
    /**
     * Text entry for an empty fluid tank.
     */
    TankEmpty("Empty", Type.TOOLTIP),
    /**
     * Text entry for displaying amounts of fluids in a fluid tank.
     */
    TankAmount("%s / %s B", Type.TOOLTIP),
    /**
     * Text entry for setting an amount.
     */
    SetAmount("Set Amount", Type.GUI),
    /**
     * Text entry for the error message of the hex color input.
     */
    InvalidHexInput("Invalid hex code input", Type.TOOLTIP);

    private final String englishText;
    private final Type type;

    LibText(String englishText, Type type) {
        this.englishText = englishText;
        this.type = type;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return String.format("%s.%s.%s", type.root, AE2AddonLib.MOD_ID, name());
    }

    private enum Type {
        GUI("gui"),
        TOOLTIP("gui.tooltips"),
        EMI_CATEGORY("emi.category"),
        EMI_TEXT("emi.text");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
