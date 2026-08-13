package dev.thestaticvoid.stcm.entity;

import aztech.modern_industrialization.MIBlock;
import dev.thestaticvoid.stcm.STCMConfig;
import dev.thestaticvoid.stcm.world.STCMWorld;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class PrimedNuke extends Entity implements TraceableEntity {
    static {
        DATA_FUSE_ID = SynchedEntityData.defineId(PrimedNuke.class, EntityDataSerializers.INT);
        DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(PrimedNuke.class, EntityDataSerializers.BLOCK_STATE);
    }

    public static final float EXPLOSION_FORCE = 32.0F;
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID;
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID;
    private static final int DEFAULT_FUSE_TIME = 160;
    private static final String TAG_BLOCK_STATE = "block_state";
    public static final String TAG_FUSE = "fuse";
    private final DamageSource DAMAGE_SOURCE = new DamageSource(registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(STCMWorld.NUKE_DAMAGE));
    @Nullable
    private LivingEntity owner;

    public PrimedNuke(EntityType<? extends PrimedNuke> entityType, Level level) {
        super(entityType, level);
    }

    public PrimedNuke(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        this(STCMEntity.PRIMED_NUKE.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (float) (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(d0) * 0.02, 0.2F, -Math.cos(d0) * 0.02);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
    }

    @Override
    public void tick() {
        this.handlePortal();
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    protected void explode() {
        if (!STCMConfig.CONFIG.nukeBlockDamage.get()) {
            return;
        }
        this.level().explode(
                this,
                DAMAGE_SOURCE,
                null,
                this.getX(),
                this.getY((double) 0.0625F),
                this.getZ(),
                EXPLOSION_FORCE,
                true,
                Level.ExplosionInteraction.BLOCK
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
        builder.define(DATA_BLOCK_STATE_ID, MIBlock.NUKE.get().defaultBlockState());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setFuse(compoundTag.getShort(TAG_FUSE));
        if (compoundTag.contains(TAG_BLOCK_STATE, 10)) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compoundTag.getCompound("block_state")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putShort(TAG_FUSE, (short)this.getFuse());
        compoundTag.put(TAG_BLOCK_STATE, NbtUtils.writeBlockState(this.getBlockState()));
    }


    public void setFuse(int life) {
        this.entityData.set(DATA_FUSE_ID, life);
    }

    public int getFuse() {
        return (Integer)this.entityData.get(DATA_FUSE_ID);
    }

    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public BlockState getBlockState() {
        return (BlockState)this.entityData.get(DATA_BLOCK_STATE_ID);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.owner;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }
}
