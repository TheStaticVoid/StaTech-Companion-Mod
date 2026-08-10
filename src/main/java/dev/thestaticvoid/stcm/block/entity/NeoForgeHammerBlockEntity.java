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
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_TOOL = 1;
    private static final int SLOT_OUTPUT = 2;

    private NonNullList<ItemStack> items;
    private final ContainerData dataAccess;

    public NeoForgeHammerBlockEntity(BlockPos pos, BlockState blockState) {
        super(STCMBlockEntity.NEOFORGE_HAMMER_BE.get(), pos, blockState);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.dataAccess = new ContainerData() {
            @Override
            public int get(int i) {
                return 0;
            }

            @Override
            public void set(int i, int i1) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void setItem(int index, ItemStack stack) {
        ItemStack itemStack = (ItemStack) this.items.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, stack);
        this.items.set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
//        if (index == SLOT_INPUT && !flag) {
//            this.setChanged();
//        }

        if (index == SLOT_INPUT || index == SLOT_TOOL) {
            this.setChanged();
        }
    }

    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT) {
            return false;
        } else if (index == SLOT_TOOL) {
            return stack.is(ForgeTool.TAG);
        } else {
            return true;
        }
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
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }
}
