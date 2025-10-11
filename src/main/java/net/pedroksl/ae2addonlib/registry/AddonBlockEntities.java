package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.DeferredBlockEntityType;

@SuppressWarnings("unused")
public class AddonBlockEntities {

    static AddonBlockEntities INSTANCE;
    private final DeferredRegister<BlockEntityType<?>> DR;
    private static final List<DeferredBlockEntityType<?>> BLOCK_ENTITY_TYPES = new ArrayList<>();

    public AddonBlockEntities(String modId) {
        if (INSTANCE != null && FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Tried to initialize AddonBlockEntities on Client Dist.");
        }

        INSTANCE = this;

        DR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
    }

    private static AddonBlockEntities getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Block Entities Registration is not initialized.");
        }

        return INSTANCE;
    }

    static DeferredRegister<?> getDR() {
        return getInstance().DR;
    }

    /**
     * Get all block entity types whose implementations extends the given base class.
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> List<BlockEntityType<? extends T>> getSubclassesOf(Class<T> baseClass) {
        var result = new ArrayList<BlockEntityType<? extends T>>();
        for (var type : BLOCK_ENTITY_TYPES) {
            if (baseClass.isAssignableFrom(type.getBlockEntityClass())) {
                result.add((BlockEntityType<? extends T>) type.get());
            }
        }
        return result;
    }

    /**
     * Get all block entity types whose implementations implement the given interface.
     */
    public static List<BlockEntityType<?>> getImplementorsOf(Class<?> iface) {
        var result = new ArrayList<BlockEntityType<?>>();
        for (var type : BLOCK_ENTITY_TYPES) {
            if (iface.isAssignableFrom(type.getBlockEntityClass())) {
                result.add(type.get());
            }
        }
        return result;
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    @SafeVarargs
    protected static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        if (blockDefs.length == 0) {
            throw new IllegalArgumentException();
        }

        var deferred = getInstance().DR.register(id, () -> {
            var blocks = Arrays.stream(blockDefs).map(BlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

            var typeHolder = new AtomicReference<BlockEntityType<T>>();
            var type = BlockEntityType.Builder.of((pos, state) -> factory.create(typeHolder.get(), pos, state), blocks)
                    .build(null);
            typeHolder.setPlain(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefs[0].asItem());

            for (var block : blocks) {
                block.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });

        var result = new DeferredBlockEntityType<>(entityClass, deferred);
        BLOCK_ENTITY_TYPES.add(result);
        return result;
    }

    protected interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    public void register(IEventBus eventBus) {
        getDR().register(eventBus);
    }
}
