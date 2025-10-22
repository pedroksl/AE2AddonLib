package net.pedroksl.ae2addonlib.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.PatternProviderBlock;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2BlockStateProvider;

/**
 * Utility class that provides methods to help in generating json models.
 */
public abstract class AE2AddonModelProvider extends AE2BlockStateProvider {

    /**
     * Constructs the provider's instance.
     * @param packOutput The mod's pack output.
     * @param modId The MOD_ID of the child mod.
     * @param exFileHelper The mod's file helper.
     */
    public AE2AddonModelProvider(PackOutput packOutput, String modId, ExistingFileHelper exFileHelper) {
        super(packOutput, modId, exFileHelper);
    }

    /**
     * Constructs a model for a simple item from a {@link ItemDefinition}.
     * @param item The item's definition.
     */
    protected void basicItem(ItemDefinition<?> item) {
        basicItem(item, null);
    }

    /**
     * Constructs a model for a simple, optionally referencing a different texture path.
     * @param item The item's definition.
     * @param texturePath The path of the textures for the item.
     */
    protected void basicItem(ItemDefinition<?> item, String texturePath) {
        if (texturePath == null) itemModels().basicItem(item.asItem());
        else {
            String namespace = item.id().getNamespace();
            String id = item.id().getPath();
            ResourceLocation texture =
                    ResourceLocation.fromNamespaceAndPath(namespace, "item/" + texturePath + "/" + id);
            itemModels().singleTexture(id, mcLoc("item/generated"), "layer0", texture);
        }
    }

    /**
     * Constructs a model for a colored item. Generates a model with two layers. The base layer will look for a texture
     * ending in "_base". The colored layer will look for a texture ending in "_tint".
     * @param item The item's definition.
     */
    protected void coloredItem(ItemDefinition<?> item) {
        String namespace = item.id().getNamespace();
        String id = item.id().getPath();
        ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(namespace, "item/" + id + "_base");
        ResourceLocation tintTexture = ResourceLocation.fromNamespaceAndPath(namespace, "item/" + id + "_tint");
        itemModels()
                .singleTexture(id, mcLoc("item/generated"), "layer0", baseTexture)
                .texture("layer1", tintTexture);
    }

    /**
     * Constructs a model for a simple block from a {@link BlockDefinition}.
     * @param block The block's definition.
     */
    protected void basicBlock(BlockDefinition<?> block) {
        simpleBlockAndItem(block);
    }

    /**
     * Convenience version of {@link #partItem(ItemDefinition, boolean)} that assumes the item isn't an export bus.
     * @param part The part's definition.
     */
    protected void partItem(ItemDefinition<?> part) {
        partItem(part, false);
    }

    /**
     * <p>Constructs a model for a part item.</p>
     * Assumes the parts id end in "_part". The resulting block state will use resource locations ending in "_back" and
     * "_sides" for the back and side textures respectively.
     * @param part The part's definition.
     * @param isBus Tells the method if the model is for an export bus.
     */
    protected void partItem(ItemDefinition<?> part, boolean isBus) {
        var namespace = part.id().getNamespace();
        var id = part.id().getPath();
        var partName = id.substring(0, id.lastIndexOf('_'));
        var front = ResourceLocation.fromNamespaceAndPath(namespace, "part/" + partName);
        var back = ResourceLocation.fromNamespaceAndPath(namespace, "part/" + partName + "_back");
        var sides = ResourceLocation.fromNamespaceAndPath(namespace, "part/" + partName + "_sides");

        var base = isBus ? AppEng.makeId("part/export_bus_base") : AppEng.makeId("part/pattern_provider_base");
        var itemBase = isBus ? AppEng.makeId("item/export_bus") : AppEng.makeId("item/cable_pattern_provider");

        models().singleTexture("part/" + id, base, "sidesStatus", AppEng.makeId("part/monitor_sides_status"))
                .texture("sides", sides)
                .texture("front", front)
                .texture("back", back)
                .texture("particle", back);
        itemModels()
                .singleTexture("item/" + id, itemBase, "sides", sides)
                .texture("front", front)
                .texture("back", back);
    }

    /**
     * Constructs a pattern provider model from a {@link BlockDefinition}.
     * @param block The pattern provider's definition.
     */
    protected void patternProvider(BlockDefinition<?> block) {
        var patternProviderNormal = cubeAll(block.block());
        simpleBlockItem(block.block(), patternProviderNormal);

        var namespace = block.id().getNamespace();
        var blockName = block.id().getPath();
        var patternProviderOriented = models().cubeBottomTop(
                        "block/" + blockName + "_oriented",
                        ResourceLocation.fromNamespaceAndPath(namespace, "block/" + blockName + "_alt"),
                        ResourceLocation.fromNamespaceAndPath(namespace, "block/" + blockName + "_back"),
                        ResourceLocation.fromNamespaceAndPath(namespace, "block/" + blockName + "_front"));
        multiVariantGenerator(block, Variant.variant())
                .with(PropertyDispatch.property(PatternProviderBlock.PUSH_DIRECTION)
                        .generate((dir) -> {
                            var forward = dir.getDirection();
                            if (forward == null) {
                                return Variant.variant()
                                        .with(VariantProperties.MODEL, patternProviderNormal.getLocation());
                            } else {
                                var orientation = BlockOrientation.get(forward);
                                return applyRotation(
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, patternProviderOriented.getLocation()),
                                        // + 90 because the default model is oriented UP, while block orientation
                                        // assumes NORTH
                                        orientation.getAngleX() + 90,
                                        orientation.getAngleY(),
                                        0);
                            }
                        }));
    }

    @Override
    protected void stairsBlock(
            BlockDefinition<StairBlock> stairs, String bottomTexture, String sideTexture, String topTexture) {
        String namespace = stairs.id().getNamespace();
        String baseName = stairs.id().getPath();
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(namespace, sideTexture);
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(namespace, bottomTexture);
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(namespace, topTexture);
        ModelFile stairsModel = this.models().stairs(baseName, side, bottom, top);
        ModelFile stairsInner = this.models().stairsInner(baseName + "_inner", side, bottom, top);
        ModelFile stairsOuter = this.models().stairsOuter(baseName + "_outer", side, bottom, top);
        this.stairsBlock(stairs.block(), stairsModel, stairsInner, stairsOuter);
        this.simpleBlockItem(stairs.block(), stairsModel);
    }

    @Override
    protected void slabBlock(
            BlockDefinition<SlabBlock> slab,
            BlockDefinition<?> base,
            String bottomTexture,
            String sideTexture,
            String topTexture) {
        String namespace = slab.id().getNamespace();
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(namespace, sideTexture);
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(namespace, bottomTexture);
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(namespace, topTexture);
        BlockModelBuilder bottomModel = this.models().slab(slab.id().getPath(), side, bottom, top);
        this.simpleBlockItem(slab.block(), bottomModel);
        this.slabBlock(
                slab.block(),
                bottomModel,
                this.models().slabTop(slab.id().getPath() + "_top", side, bottom, top),
                this.models().getExistingFile(base.id()));
    }

    @Override
    protected void wall(BlockDefinition<WallBlock> block, String texture) {
        String namespace = block.id().getNamespace();
        ResourceLocation textureRL = ResourceLocation.fromNamespaceAndPath(namespace, texture);
        wallBlock(block.block(), textureRL);
        itemModels().wallInventory(block.id().getPath(), textureRL);
    }

    /**
     * Constructs a fluid model from a {@link FluidDefinition}.
     * @param fluid The fluid's definition.
     */
    protected void waterBasedFluid(FluidDefinition<?, ?> fluid) {
        waterBasedFluidBlocks(fluid);
        bucket(fluid);
    }

    /**
     * Constructs a fluid block model from a {@link FluidDefinition}.
     * @param fluid The fluid's definition.
     */
    protected void waterBasedFluidBlocks(FluidDefinition<?, ?> fluid) {
        simpleBlock(
                fluid.block(),
                models().getBuilder(fluid.blockHolder().getId().getPath())
                        .texture("particle", AE2AddonLib.makeId(ModelProvider.BLOCK_FOLDER + "/" + "water_still")));
    }

    /**
     * Constructs a bucket of fluid from a {@link FluidDefinition}.
     * @param fluid The fluid's definition.
     */
    protected void bucket(FluidDefinition<?, ?> fluid) {
        itemModels()
                .withExistingParent(
                        fluid.bucketItemId().id().getPath(),
                        ResourceLocation.fromNamespaceAndPath(NeoForgeVersion.MOD_ID, "item/bucket"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(fluid.bucketItem().content);
    }

    @Override
    public String getName() {
        return "Block States / Models";
    }
}
