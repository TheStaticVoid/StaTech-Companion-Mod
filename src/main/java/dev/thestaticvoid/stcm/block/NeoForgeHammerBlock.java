package dev.thestaticvoid.stcm.block;

import com.mojang.serialization.MapCodec;
import dev.thestaticvoid.stcm.block.entity.NeoForgeHammerBlockEntity;
import dev.thestaticvoid.stcm.block.entity.STCMBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class NeoForgeHammerBlock extends BaseEntityBlock {
    private VoxelShape shape;
    private int part_height[] = { 4, 1, 5, 5 };
    private int part_width[] = { 14, 10, 8, 14 };
    public static final MapCodec<NeoForgeHammerBlock> CODEC = simpleCodec(NeoForgeHammerBlock::new);

    public NeoForgeHammerBlock(Properties properties) {
        super(properties);
        VoxelShape[] parts = new VoxelShape[part_height.length];
        float currentY = 0;
        for (int i = 0; i < part_height.length; i++) {
            float o = (16 - part_width[i]) / 32.0f;
            float e = o + part_width[i] / 16.0f;
            parts[i] = Shapes.box(o, currentY, o, e, currentY + part_height[i] / 16.0f, e);
            currentY += part_height[i] / 16.0f;
        }
        shape = parts[0];
        for (int i = 1; i < part_height.length; i++) {
            shape = Shapes.or(shape, parts[i]);
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new NeoForgeHammerBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return super.isPathfindable(state, pathComputationType);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof NeoForgeHammerBlockEntity efhBlockEntity) {
                efhBlockEntity.dropInventory();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof NeoForgeHammerBlockEntity blockEntity) {
                ((ServerPlayer)player).openMenu(new SimpleMenuProvider(blockEntity, Component.translatable("gui.stcm.neoforge_hammer")), pos);
                return InteractionResult.CONSUME;
            } else {
                throw new IllegalStateException("Block at position is not NeoForgeHammerBlockEntity");
            }
        }
    }

//    @Override
//    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
//        if (level.isClientSide()) {
//            return null;
//        }
//
//        return createTickerHelper(blockEntityType, STCMBlockEntity.NEOFORGE_HAMMER_BE.get(),
//                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
//    }
}