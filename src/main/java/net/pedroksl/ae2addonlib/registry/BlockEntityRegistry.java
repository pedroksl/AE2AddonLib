package net.pedroksl.ae2addonlib.registry;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;

/**
 * <p>Class responsible for the registering of block entities.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class BlockEntityRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    private static final Map<String, DeferredRegister<BlockEntityType<?>>> DRMap = new HashMap<>();
    private static final Map<String, List<BlockEntityType<?>>> BLOCK_ENTITY_TYPES_MAP = new HashMap<>();
    private final String modId;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and BER map.
     * @param modId The MOD_ID of the mod creating this instance.
     */
    public BlockEntityRegistry(String modId) {
        if (DRMap.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize AddonBlockEntities on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        DRMap.put(modId, DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId));
        BLOCK_ENTITY_TYPES_MAP.put(modId, new ArrayList<>());
    }

    static DeferredRegister<BlockEntityType<?>> getDR(String modId) {
        var dr = DRMap.getOrDefault(modId, null);
        if (dr == null) {
            LOG.error("Tried to access uninitialized deferred register with mod id {}", modId);
            throw new IllegalStateException();
        }
        return dr;
    }

    /**
     * Non-static version of {@link #getSubclassesOf(String, Class)}.
     * @param baseClass The base class
     * @param <T> Class that extends a {@link BlockEntity}
     * @return A list containing matching block entity types.
     */
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> List<BlockEntityType<? extends T>> getSubclassesOf(Class<T> baseClass) {
        return getSubclassesOf(this.modId, baseClass);
    }

    /**
     * Get all block entity types whose implementations extends the given base class.
     * @param modId The MOD_ID of the requesting mod.
     * @param baseClass The base class
     * @param <T> Class that extends a {@link BlockEntity}
     * @return A list containing matching block entity types.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> List<BlockEntityType<? extends T>> getSubclassesOf(
            String modId, Class<T> baseClass) {
        var result = new ArrayList<BlockEntityType<? extends T>>();
        for (var type : BLOCK_ENTITY_TYPES_MAP.getOrDefault(modId, new ArrayList<>())) {
            if (baseClass.isAssignableFrom(type.getClass())) {
                result.add((BlockEntityType<? extends T>) type);
            }
        }
        return result;
    }

    /**
     * Non-static version of {@link #getImplementorsOf(String, Class)};
     * @param iface The parent interface
     * @return A list containing matching block entity types.
     */
    public List<BlockEntityType<?>> getImplementorsOf(Class<?> iface) {
        return getImplementorsOf(this.modId, iface);
    }

    /**
     * Get all block entity types whose implementations implement the given interface.
     * @param modId The MOD_ID of the requesting mod.
     * @param iface The parent interface
     * @return A list containing matching block entity types.
     */
    public static List<BlockEntityType<?>> getImplementorsOf(String modId, Class<?> iface) {
        var result = new ArrayList<BlockEntityType<?>>();
        for (var type : BLOCK_ENTITY_TYPES_MAP.getOrDefault(modId, new ArrayList<>())) {
            if (iface.isAssignableFrom(type.getClass())) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * Registers the block entity into the deferred registered and saves the block entity type for later use.
     * Additionally, links the BlockEntity to the provided Blocks.
     * @param modId The MOD_ID of the requesting mod.
     * @param id The id string of the block entity.
     * @param entityClass The class of the block entity.
     * @param factory Factory method capable of creating a new instance of the block entity.
     * @param blockDefs A list of {@link LibBlockDefinition} containing the blocks that share this block entity.
     * @param <T> The class of the block entity.
     * @return A supplier of this {@link BlockEntityType}.
     */
    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    @SafeVarargs
    protected static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String modId,
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            LibBlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        if (blockDefs.length == 0) {
            throw new IllegalArgumentException();
        }

        return getDR(modId).register(id, () -> {
            var blocks = Arrays.stream(blockDefs).map(LibBlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

            var typeHolder = new AtomicReference<BlockEntityType<T>>();
            var type = BlockEntityType.Builder.of((pos, state) -> factory.create(typeHolder.get(), pos, state), blocks)
                    .build(null);
            typeHolder.setPlain(type);
            BLOCK_ENTITY_TYPES_MAP.get(modId).add(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefs[0].asItem());

            for (var block : blocks) {
                block.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }

    /**
     * Interface used to encapsulate the constructor of a {@link BlockEntity}.
     * @param <T> A BlockEntity that is a children of {@link AEBaseBlockEntity}.
     */
    protected interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        /** Encapsulation of a block entity constructor.
         * @param type The type of this block entity
         * @param pos The in-world position of this block entity.
         * @param state The initial {@link BlockState}.
         * @return A new BlockEntity instance.
         */
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    /**
     * Used to finalize the block entity registration.
     * Should be called by the inheritor's static instance.
     * @param eventBus The bus received as a parameter in the mod's main constructor.
     */
    public void register(IEventBus eventBus) {
        getDR(this.modId).register(eventBus);
    }
}
