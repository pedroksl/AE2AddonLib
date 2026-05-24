package net.pedroksl.ae2addonlib.util;

/**
 * <p>Simple implementation of a water-based fluid.</p>
 * This class provides the basic components to render a fluid that uses the same textures as water.
 * The class can be extended to be customized by adding a tint and changing fluid properties.
 */
public interface WaterBasedFluidType {

    /**
     * @return The color used to tint this fluid
     */
    default int getTintColor() {
        return -1;
    }
}
