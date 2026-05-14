package net.pedroksl.ae2addonlib.datagen;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

import java.util.Optional;

import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.helpers.FluidDefinition;

import appeng.api.orientation.BlockOrientation;
import appeng.api.util.AEColor;
import appeng.api.util.AEColorVariant;
import appeng.block.crafting.PatternProviderBlock;
import appeng.client.render.AEColorItemTintSource;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.ModelSubProvider;
import appeng.datagen.providers.models.PartModelOutput;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;

/**
 * Utility class that provides methods to help in generating json models.
 */
public abstract class AE2AddonModelProvider extends ModelSubProvider {

    public static final TextureSlot SIDES = TextureSlot.create("sides", TextureSlot.ALL);
    public static final ModelTemplate BUS_TEMPLATE = new ModelTemplate(
            Optional.of(AppEng.makeId("part/export_bus_base")),
            Optional.empty(),
            TextureSlot.BACK,
            TextureSlot.FRONT,
            TextureSlot.PARTICLE,
            SIDES);

    public static final ModelTemplate BUS_ITEM_TEMPLATE = new ModelTemplate(
            Optional.of(AppEng.makeId("item/export_bus")), Optional.empty(), SIDES, TextureSlot.FRONT);

    public static final ModelTemplate PP_TEMPLATE = new ModelTemplate(
            Optional.of(AppEng.makeId("part/pattern_provider_base")),
            Optional.empty(),
            TextureSlot.BACK,
            TextureSlot.FRONT,
            TextureSlot.PARTICLE,
            SIDES);

    public static final ModelTemplate PP_ITEM_TEMPLATE = new ModelTemplate(
            Optional.of(AppEng.makeId("item/cable_interface")),
            Optional.empty(),
            SIDES,
            TextureSlot.FRONT,
            TextureSlot.BACK,
            TextureSlot.PARTICLE);

    /**
     * Constructs the provider's instance.
     * @param blockModels The Block Model Generators.
     * @param itemModels The Item Model Generators.
     * @param partModels The Part Model Output.
     */
    public AE2AddonModelProvider(
            BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
        super(blockModels, itemModels, partModels);
    }

    /**
     * Constructs a model for a simple item from a {@link ItemDefinition}.
     * @param item The item's definition.
     */
    protected void basicItem(ItemDefinition<?> item) {
        basicItem(item, "item/" + item.id().getPath());
    }

    /**
     * Constructs a model for a simple, optionally referencing a different texture path.
     * @param item The item's definition.
     * @param texture The path of the textures for the item.
     */
    protected void basicItem(ItemDefinition<?> item, String texture) {
        var textureId = Identifier.fromNamespaceAndPath(item.id().getNamespace(), texture);
        var model = ModelTemplates.FLAT_ITEM.create(
                item.asItem(), TextureMapping.layer0(new Material(textureId)), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }

    /**
     * Constructs a model for a colored item. Generates a model with two layers. The base layer will look for a texture
     * ending in "_base". The colored layer will look for a texture ending in "_tint".
     * @param item The item's definition.
     */
    protected void twoLayeredItem(ItemDefinition<?> item) {
        String namespace = item.id().getNamespace();
        String id = item.id().getPath();
        Identifier baseTexture = Identifier.fromNamespaceAndPath(namespace, "item/" + id + "_base");
        Identifier tintTexture = Identifier.fromNamespaceAndPath(namespace, "item/" + id + "_tint");

        var model = ModelTemplates.TWO_LAYERED_ITEM.create(
                item.asItem(),
                TextureMapping.layered(new Material(baseTexture), new Material(tintTexture)),
                itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.tintedModel(model));
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
        var front = new Material(Identifier.fromNamespaceAndPath(namespace, "part/" + partName));
        var back = new Material(Identifier.fromNamespaceAndPath(namespace, "part/" + partName + "_back"));
        var sides = new Material(Identifier.fromNamespaceAndPath(namespace, "part/" + partName + "_sides"));

        var mapping = new TextureMapping()
                .put(TextureSlot.BACK, back)
                .put(TextureSlot.FRONT, front)
                .put(TextureSlot.PARTICLE, back)
                .put(SIDES, sides);

        Identifier partId = Identifier.fromNamespaceAndPath(namespace, "part/" + id);
        Identifier itemId = Identifier.fromNamespaceAndPath(namespace, "item/" + id);
        Identifier model;
        Identifier itemModel;
        if (isBus) {
            model = BUS_TEMPLATE.create(partId, mapping, modelOutput);
            itemModel = BUS_ITEM_TEMPLATE.create(itemId, mapping, modelOutput);
        } else {
            model = PP_TEMPLATE.create(partId, mapping, modelOutput);
            itemModel = PP_ITEM_TEMPLATE.create(itemId, mapping, modelOutput);
        }
        partModels.staticModel(part, model);

        if (!(part.asItem() instanceof PartItem<?> item)) {
            return;
        }

        var color = AEColor.TRANSPARENT;
        if (item instanceof ColoredPartItem<?> coloredPartItem) {
            color = coloredPartItem.getColor();
        }

        itemModels.itemModelOutput.accept(
                item.asItem(),
                ItemModelUtils.tintedModel(
                        itemModel,
                        new Constant(-1),
                        new AEColorItemTintSource(color, AEColorVariant.DARK),
                        new AEColorItemTintSource(color, AEColorVariant.MEDIUM),
                        new AEColorItemTintSource(color, AEColorVariant.BRIGHT),
                        new AEColorItemTintSource(color, AEColorVariant.MEDIUM_BRIGHT)));
    }

    /**
     * Constructs a pattern provider model from a {@link BlockDefinition}.
     * @param block The pattern provider's definition.
     */
    protected void patternProvider(BlockDefinition<?> block) {
        var normalModel = TexturedModel.CUBE.create(block.block(), modelOutput);

        var namespace = block.id().getNamespace();
        var blockName = block.id().getPath();

        var orientedModel = TexturedModel.CUBE_TOP_BOTTOM
                .updateTexture(textures -> textures.put(
                                TextureSlot.SIDE,
                                new Material(Identifier.fromNamespaceAndPath(namespace, "block/" + blockName + "_alt")))
                        .put(
                                TextureSlot.BOTTOM,
                                new Material(
                                        Identifier.fromNamespaceAndPath(namespace, "block/" + blockName + "_back")))
                        .put(
                                TextureSlot.TOP,
                                new Material(
                                        Identifier.fromNamespaceAndPath(namespace, "block/" + blockName + "_back"))))
                .createWithSuffix(block.block(), "_oriented", modelOutput);

        blockStateOutput.accept(MultiVariantGenerator.dispatch(block.block())
                .with(PropertyDispatch.initial(PatternProviderBlock.PUSH_DIRECTION)
                        .generate((dir) -> {
                            var forward = dir.getDirection();
                            if (forward == null) {
                                return plainVariant(normalModel);
                            } else {
                                var orientation = BlockOrientation.get(forward);
                                return plainVariant(orientedModel)
                                        .with(applyRotation(
                                                // + 90 because the default model is oriented UP, while block
                                                // orientation assumes NORTH
                                                orientation.getAngleX() + 90, orientation.getAngleY()));
                            }
                        })));
    }

    protected void stairsBlock(BlockDefinition<? extends StairBlock> stairs, BlockDefinition<?> templateBlock) {
        var blockTexture = getBlockTexture(templateBlock);
        stairsBlock(stairs, blockTexture, blockTexture, blockTexture);
    }

    protected void stairsBlock(
            BlockDefinition<? extends StairBlock> stairs,
            Material bottomTexture,
            Material sideTexture,
            Material topTexture) {
        var block = stairs.block();

        var textures = new TextureMapping()
                .put(TextureSlot.TOP, topTexture)
                .put(TextureSlot.BOTTOM, bottomTexture)
                .put(TextureSlot.SIDE, sideTexture);

        var straightModel = ModelTemplates.STAIRS_STRAIGHT.create(block, textures, modelOutput);
        var innerModel = ModelTemplates.STAIRS_INNER.create(block, textures, modelOutput);
        var outerModel = ModelTemplates.STAIRS_OUTER.create(block, textures, modelOutput);

        blockModels.blockStateOutput.accept(
                createStairs(block, plainVariant(innerModel), plainVariant(straightModel), plainVariant(outerModel)));
        blockModels.registerSimpleItemModel(block, straightModel);
    }

    protected void slabBlock(BlockDefinition<? extends SlabBlock> slab, BlockDefinition<?> baseBlock) {
        var texture = getBlockTexture(baseBlock);
        slabBlock(slab, baseBlock, texture, texture, texture);
    }

    private void slabBlock(
            BlockDefinition<? extends SlabBlock> slab,
            BlockDefinition<?> doubleModelDonor,
            Material topTexture,
            Material sideTexture,
            Material bottomTexture) {
        var block = slab.block();

        var textures = new TextureMapping()
                .put(TextureSlot.TOP, topTexture)
                .put(TextureSlot.BOTTOM, bottomTexture)
                .put(TextureSlot.SIDE, sideTexture);

        var topModel = plainVariant(ModelTemplates.SLAB_TOP.create(block, textures, modelOutput));
        var bottomModel = ModelTemplates.SLAB_BOTTOM.create(block, textures, modelOutput);

        blockModels.blockStateOutput.accept(createSlab(
                block,
                plainVariant(bottomModel),
                topModel,
                plainVariant(ModelLocationUtils.getModelLocation(doubleModelDonor.block()))));
        blockModels.registerSimpleItemModel(block, bottomModel);
    }

    protected void wall(BlockDefinition<? extends WallBlock> wall, Material texture) {
        var block = wall.block();

        var textures = new TextureMapping().put(TextureSlot.WALL, texture);

        var lowSideModel = plainVariant(ModelTemplates.WALL_LOW_SIDE.create(block, textures, modelOutput));
        var postModel = plainVariant(ModelTemplates.WALL_POST.create(block, textures, modelOutput));
        var tallSideModel = plainVariant(ModelTemplates.WALL_TALL_SIDE.create(block, textures, modelOutput));

        blockModels.blockStateOutput.accept(createWall(block, postModel, lowSideModel, tallSideModel));

        var invModel = ModelTemplates.WALL_INVENTORY.create(block, textures, modelOutput);
        blockModels.registerSimpleItemModel(block, invModel);
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
        blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(
                fluid.block(),
                plainVariant(ModelTemplates.PARTICLE_ONLY.create(
                        fluid.block(),
                        TextureMapping.particle(new Material(AE2AddonLib.makeId("block/water_still"))),
                        this.modelOutput))));
    }

    /**
     * Constructs a bucket of fluid from a {@link FluidDefinition}.
     * @param fluid The fluid's definition.
     */
    protected void bucket(FluidDefinition<?, ?> fluid) {
        itemModels.itemModelOutput.accept(
                fluid.bucketItem(),
                new DynamicFluidContainerModel.Unbaked(
                        new DynamicFluidContainerModel.Textures(
                                Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                                Optional.of(new Material(Identifier.withDefaultNamespace("item/bucket"))),
                                Optional.of(new Material(
                                        Identifier.fromNamespaceAndPath("neoforge", "item/mask/bucket_fluid"))),
                                Optional.empty()),
                        fluid.source(),
                        false,
                        true,
                        false));
    }
}
