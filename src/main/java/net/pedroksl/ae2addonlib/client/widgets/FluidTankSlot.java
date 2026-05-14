package net.pedroksl.ae2addonlib.client.widgets;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.pedroksl.ae2addonlib.core.network.serverPacket.FluidTankItemUsePacket;
import net.pedroksl.ae2addonlib.datagen.LibText;

import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.Blitter;
import appeng.core.localization.Tooltips;

/**
 * A fluid tank slot. This slot must be constructed in the screen class, which must implement the {@link net.pedroksl.ae2addonlib.api.IFluidTankScreen} interface.
 * That screen needs to be linked to a menu that implements the {@link net.neoforged.neoforge.fluids.capability.IFluidHandler} interface.
 * The two interfaces provide functionality to handle item use and playing sounds.
 */
public class FluidTankSlot extends AbstractWidget {

    private final AbstractContainerScreen<?> screen;
    private TextureAtlasSprite fluidTexture;
    private int fluidTint = -1;
    private FluidStack content = FluidStack.EMPTY;
    private final int maxLevel;
    private boolean disableRender = false;
    /**
     * The tank's index.
     */
    public final int index;

    /**
     * Constructs a fluid tank slot with initial values.
     * @param screen The screen the tank is attached to.
     * @param index The tank's index.
     * @param x The left-most coordinate of the tank.
     * @param y The top-most coordinate of the tank.
     * @param width The width of the tank.
     * @param height The height of the tank.
     * @param maxLevel The max level of the tank (in buckets).
     */
    public FluidTankSlot(
            AbstractContainerScreen<?> screen, int index, int x, int y, int width, int height, int maxLevel) {
        super(x, y, width, height, Component.empty());
        this.maxLevel = maxLevel;
        this.screen = screen;
        this.index = index;
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
        var stack = screen.getMenu().getCarried();
        if (isValidClickButton(event.button()) && !stack.isEmpty()) {
            if (!FluidUtil.getFirstStackContained(stack).isEmpty()) {
                FluidStack fluidStack = FluidUtil.getFirstStackContained(stack);
                if (fluidStack.is(this.content.getFluid()) || fluidStack.isEmpty() || this.content.isEmpty()) {
                    var actualButton = screen instanceof AEBaseScreen<?> baseScreen
                            ? (baseScreen.isHandlingRightClick() ? 1 : 0)
                            : event.button();
                    ClientPacketDistributor.sendToServer(new FluidTankItemUsePacket(this.index, actualButton));
                }
            }
        }
    }

    public boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (content == null || fluidTexture == null || this.disableRender) return;

        float fluidHeight = content.getAmount() / 1000f / maxLevel * this.height;
        Blitter.sprite(this.fluidTexture)
                .dest(this.getX(), (int) (this.getY() + this.height - fluidHeight), this.width, (int) fluidHeight)
                .colorRgb(this.fluidTint)
                .blit(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {}

    /**
     * Updates the displayed {@link FluidStack}.
     * @param fluidStack The new fluid stack.
     */
    public void setFluidStack(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.content = FluidStack.EMPTY;
            this.disableRender = true;
            updateTooltip(fluidStack);
            return;
        }

        this.disableRender = false;
        boolean updateTexture = this.content.isEmpty() || fluidStack.getFluid() != this.content.getFluid();
        this.content = fluidStack;

        updateTooltip(fluidStack);

        if (updateTexture && !this.content.isEmpty()) {
            var fluidModel = Minecraft.getInstance()
                    .getModelManager()
                    .getFluidStateModelSet()
                    .get(this.content.getFluid().defaultFluidState());

            this.fluidTexture = fluidModel.stillMaterial().sprite();

            this.fluidTint = -1;
            var tintSource = fluidModel.fluidTintSource();
            if (tintSource != null) {
                this.fluidTint = tintSource.colorAsStack(fluidStack);
            }
        }
    }

    private void updateTooltip(FluidStack stack) {
        if (stack.isEmpty()) {
            setTooltip(Tooltip.create(Tooltips.of(
                    LibText.TankEmpty.text(),
                    Component.literal("\n"),
                    LibText.TankAmount.text(0, this.maxLevel).withStyle(Tooltips.NUMBER_TEXT))));
            return;
        }

        var genericStack = GenericStack.fromFluidStack(content);
        if (genericStack != null) {
            setTooltip(Tooltip.create(Tooltips.of(
                    stack.getHoverName(),
                    Component.literal("\n"),
                    LibText.TankAmount.text(
                                    genericStack.what().formatAmount(genericStack.amount(), AmountFormat.SLOT),
                                    this.maxLevel)
                            .withStyle(Tooltips.NUMBER_TEXT),
                    Component.literal("\n"),
                    Component.literal(
                                    getModDisplayNameFromId(genericStack.what().getModId()))
                            .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC))));
        }
    }

    @SuppressWarnings("deprecation")
    private static String getModDisplayNameFromId(String modId) {
        var container = ModList.get().getModContainerById(modId);

        if (modId.equals("c")) {
            return "Common";
        } else if ((container = ModList.get().getModContainerById(modId)).isPresent()) {
            return container.get().getModInfo().getDisplayName();
        } else {
            container = ModList.get().getModContainerById(modId.replace('_', '-'));
            return container.isPresent()
                    ? container.get().getModInfo().getDisplayName()
                    : WordUtils.capitalizeFully(modId.replace('_', ' '));
        }
    }
}
