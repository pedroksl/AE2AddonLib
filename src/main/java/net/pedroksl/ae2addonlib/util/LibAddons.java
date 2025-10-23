package net.pedroksl.ae2addonlib.util;

/**
 * The lib's implementation of a mod interaction enum.
 */
public enum LibAddons implements AddonEnum {
    /**
     * Dark mode everywhere entry, used to disable dark mode using {@link net.minecraftforge.fml.InterModComms}.
     */
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
