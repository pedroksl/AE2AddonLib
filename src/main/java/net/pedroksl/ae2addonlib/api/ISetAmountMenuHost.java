package net.pedroksl.ae2addonlib.api;

/**
 * Interface used to mark menus that make use of the {@link net.pedroksl.ae2addonlib.gui.SetAmountMenu}.
 */
public interface ISetAmountMenuHost {

    /**
     * Method called when trying to return to the correct menu.
     * Implementors should open the correct menu from this call.
     */
    void returnFromSetAmountMenu();
}
