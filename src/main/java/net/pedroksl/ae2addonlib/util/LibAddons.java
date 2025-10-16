package net.pedroksl.ae2addonlib.util;

public enum LibAddons implements AddonEnum {
    DARKMODEEVERYWHERE("Dark Mode Everywhere");

    private final String modName;

    LibAddons(String modName) {
        this.modName = modName;
    }

    @Override
    public String getModId() {
        return name().toLowerCase();
    }

    @Override
    public String getModName() {
        return this.modName;
    }
}
