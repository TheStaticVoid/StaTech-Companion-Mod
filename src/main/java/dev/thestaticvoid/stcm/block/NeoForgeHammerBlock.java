package dev.thestaticvoid.stcm.block;

import com.mojang.serialization.MapCodec;
import dev.thestaticvoid.stcm.block.entity.NeoForgeHammerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    protected MapCodec<NeoForgeHammerBlock> codec() {
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            this.openContainer(level, pos, player);
            return InteractionResult.CONSUME;
        }
    }

    public void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NeoForgeHammerBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof NeoForgeHammerBlockEntity) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, (NeoForgeHammerBlockEntity) blockEntity);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}