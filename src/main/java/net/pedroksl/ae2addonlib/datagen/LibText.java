package net.pedroksl.ae2addonlib.datagen;

import net.pedroksl.ae2addonlib.AE2AddonLib;

import guideme.internal.data.LocalizationEnum;

public enum LibText implements LocalizationEnum {
    ClearButton("Clear", Type.TOOLTIP),
    ClearFluidButtonHint("Flush the remaining fluid from the machine.", Type.TOOLTIP),
    ClearSidesButtonHint("Disable output from all sides of the machine.", Type.TOOLTIP),
    DirectionalOutput("Directional Output", Type.TOOLTIP),
    DirectionalOutputHint("Configure which directions are allowed for output auto-export", Type.TOOLTIP),
    Enabled("Enabled", Type.TOOLTIP),
    Disabled("Disabled", Type.TOOLTIP),
    TankEmpty("Empty", Type.TOOLTIP),
    TankAmount("%s / %s B", Type.TOOLTIP),
    SetAmount("Set Amount", Type.GUI),
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
