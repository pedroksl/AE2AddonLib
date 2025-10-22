package net.pedroksl.ae2addonlib.client.widgets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;

import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ICompositeWidget;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.client.gui.widgets.IResizableWidget;

/**
 * A widget container that can hold a list of both {@link ICompositeWidget} and {@link AbstractWidget}.
 * It works much like a {@link appeng.client.gui.WidgetContainer}, and will take control of its children, making sure they
 * receive requested event as well as keep them on screen when the screen is repopulated in case of a window resize.
 */
public class CompositeWidgetContainer implements ICompositeWidget {

    /**
     * The screen style, used to find where to position the container anchor.
     */
    protected final WidgetStyle style;

    /**
     * The position of the anchor of the container.
     */
    protected Point position;

    /**
     * The width of the container.
     */
    protected int width;

    /**
     * The height of the container.
     */
    protected int height;

    private boolean wantsAllMouseUp = false;
    private boolean wantsAllMouseDown = false;
    private boolean wantsAllMouseWheel = false;

    private final Map<Point, ICompositeWidget> widgets = new LinkedHashMap<>();
    private final Map<Point, AbstractWidget> abstractWidgets = new LinkedHashMap<>();

    /**
     * Container constructor
     * @param addWidget The method to be called when adding the widgets. This will generally be {@link appeng.client.gui.AEBaseScreen#addWidget(GuiEventListener)}.
     * @param style The screen style.
     * @param id The id of the widget, as declared in the style sheet.
     */
    public CompositeWidgetContainer(BiConsumer<String, ICompositeWidget> addWidget, ScreenStyle style, String id) {
        this.style = style.getWidget(id);
        setSize(this.style.getWidth(), this.style.getHeight());

        addWidget.accept(id, this);
    }

    /**
     * Add a {@link ICompositeWidget} to the container.
     * @param pos The top-left anchor {@link Point}.
     * @param widget The widget to be added to the container.
     */
    public void add(Point pos, ICompositeWidget widget) {
        this.widgets.put(pos, widget);

        wantsAllMouseUp = wantsAllMouseUp | widget.wantsAllMouseUpEvents();
        wantsAllMouseDown = wantsAllMouseDown | widget.wantsAllMouseDownEvents();
        wantsAllMouseWheel = wantsAllMouseWheel | widget.wantsAllMouseWheelEvents();
    }

    /**
     * Add a {@link AbstractWidget} to the container.
     * @param pos The top-left anchor {@link Point}
     * @param widget The widget to be added to the container.
     */
    public void add(Point pos, AbstractWidget widget) {
        this.abstractWidgets.put(pos, widget);
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(this.position.getX(), this.position.getY(), this.width, this.height);
    }

    @Override
    public void populateScreen(Consumer<AbstractWidget> addWidget, Rect2i bounds, AEBaseScreen<?> screen) {
        Rect2i relativeBounds = new Rect2i(0, 0, bounds.getWidth(), bounds.getHeight());

        for (var entry : abstractWidgets.entrySet()) {
            var pos = entry.getKey();
            var widget = entry.getValue();
            if (widget.isFocused()) {
                widget.setFocused(false); // Minecraft already cleared focus on the screen
            }

            // Position the widget
            relativeBounds.setPosition(bounds.getX() + pos.getX(), bounds.getY() + pos.getY());
            Point absolutePos = this.style.resolve(relativeBounds);
            if (widget instanceof IResizableWidget resizableWidget) {
                resizableWidget.move(absolutePos);
            } else {
                widget.setX(absolutePos.getX());
                widget.setY(absolutePos.getY());
            }

            addWidget.accept(widget);
        }

        relativeBounds = new Rect2i(0, 0, bounds.getWidth(), bounds.getHeight());

        for (var entry : widgets.entrySet()) {
            var pos = entry.getKey();
            var widget = entry.getValue();

            relativeBounds.setPosition(pos.getX(), pos.getY());
            widget.setPosition(this.style.resolve(relativeBounds));
            widget.populateScreen(addWidget, bounds, screen);
        }
    }

    @Override
    public void tick() {
        for (var widget : widgets.values()) {
            if (widget.isVisible()) {
                widget.tick();
            }
        }
    }

    @Override
    public void updateBeforeRender() {
        for (var widget : widgets.values()) {
            if (widget.isVisible()) {
                widget.updateBeforeRender();
            }
        }
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        for (var widget : widgets.values()) {
            if (widget.isVisible()) {
                widget.drawBackgroundLayer(guiGraphics, bounds, mouse);
            }
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics poseStack, Rect2i bounds, Point mouse) {
        for (var widget : widgets.values()) {
            if (widget.isVisible()) {
                widget.drawForegroundLayer(poseStack, bounds, mouse);
            }
        }
    }

    @Override
    public boolean onMouseDown(Point mousePos, int btn) {
        for (var widget : widgets.values()) {
            if (widget.isVisible()
                    && (widget.wantsAllMouseDownEvents() || mousePos.isIn(widget.getBounds()))
                    && widget.onMouseDown(mousePos, btn)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseUp(Point mousePos, int btn) {
        for (var widget : widgets.values()) {
            if (widget.isVisible()
                    && (widget.wantsAllMouseUpEvents() || mousePos.isIn(widget.getBounds()))
                    && widget.onMouseUp(mousePos, btn)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseDrag(Point mousePos, int btn) {
        for (var widget : widgets.values()) {
            if (widget.isVisible() && widget.onMouseDrag(mousePos, btn)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double wheelDelta) {
        // First pass: dispatch wheel event to widgets the mouse is over
        for (var widget : widgets.values()) {
            if (widget.isVisible() && mousePos.isIn(widget.getBounds()) && widget.onMouseWheel(mousePos, wheelDelta)) {
                return true;
            }
        }

        // Second pass: send the event to capturing widgets
        for (var widget : widgets.values()) {
            if (widget.isVisible() && widget.wantsAllMouseWheelEvents() && widget.onMouseWheel(mousePos, wheelDelta)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean wantsAllMouseUpEvents() {
        return wantsAllMouseUp;
    }

    @Override
    public boolean wantsAllMouseDownEvents() {
        return wantsAllMouseDown;
    }

    @Override
    public boolean wantsAllMouseWheelEvents() {
        return wantsAllMouseWheel;
    }

    @Override
    public void addExclusionZones(List<Rect2i> exclusionZones, Rect2i bounds) {
        ICompositeWidget.super.addExclusionZones(exclusionZones, bounds);

        for (var widget : widgets.values()) {
            if (widget.isVisible()) {
                widget.addExclusionZones(exclusionZones, bounds);
            }
        }
    }
}
