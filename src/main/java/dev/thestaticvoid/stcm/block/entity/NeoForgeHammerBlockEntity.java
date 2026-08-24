package dev.thestaticvoid.stcm.block.entity;

import aztech.modern_industrialization.items.ForgeTool;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class NeoForgeHammerBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private final ContainerData dataAccess;
    int currentRecipe = -1;

    public NeoForgeHammerBlockEntity(BlockPos pos, BlockState blockState) {
        super(STCMBlockEntity.NEOFORGE_HAMMER_BE.get(), pos, blockState);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int i) {
                switch(i) {
                    case 0: return NeoForgeHammerBlockEntity.this.currentRecipe;
                    default: return -1;
                }
            }

            @Override
            public void set(int i, int value) {
                switch(i) {
                    case 0: NeoForgeHammerBlockEntity.this.currentRecipe = value;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("gui.stcm.neoforge_hammer");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new NeoForgeHammerScreenHandler(id, inventory, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.currentRecipe = tag.getInt("CurrentRecipe");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putInt("CurrentRecipe", this.currentRecipe);
    }
}
