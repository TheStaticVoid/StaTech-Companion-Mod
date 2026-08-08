package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.blocks.forgehammer.ForgeHammerRecipe;
import dev.thestaticvoid.stcm.block.STCMBlock;
import dev.thestaticvoid.stcm.block.entity.NeoForgeHammerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeHammerScreenHandler extends AbstractContainerMenu {
    private final DataSlot selectedRecipe;
    private final List<RecipeHolder<ForgeHammerRecipe>> availableRecipes;

    public final Slot output;
    public final Slot tool;
    public final Slot input;
    private final ContainerLevelAccess context;
    private final Level level;
    private final Player player;
    private long lastSoundTime = 0;

    public final NeoForgeHammerBlockEntity blockEntity;
    private final ContainerData data;

    public NeoForgeHammerScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf containerData) {
        this(syncId, playerInventory, playerInventory.player.level().getBlockEntity(containerData.readBlockPos()), new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public NeoForgeHammerScreenHandler(int syncId, Inventory playerInventory, BlockEntity entity, ContainerData data, ContainerLevelAccess context) {
        super(STCMMenuTypes.NEOFORGE_HAMMER_MENU.get(), syncId);
        this.blockEntity = ((NeoForgeHammerBlockEntity) entity);
        this.level = playerInventory.player.level();
        this.data = data;
        this.context = context;
        this.selectedRecipe = DataSlot.standalone();
        this.availableRecipes = new ArrayList<>();
        this.player = playerInventory.player;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.tool = this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 8, 33));
        this.input = this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 34, 33));
        this.output = this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 2, 143, 33));

        addDataSlots(data);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        return blockEntity.clickMenuButton(player, id);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.output.container && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 38) {
                item.onCraftedBy(itemStack2, player.level(), player);
                if (!this.moveItemStackTo(itemStack2, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack2, itemStack);
            } else if (index == 37 || index == 36) {
                if (!this.moveItemStackTo(itemStack2, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 36) {
                if (!this.moveItemStackTo(itemStack2, 36, 38, true)) {
                    if (index < 27) {
                        if (!this.moveItemStackTo(itemStack2, 27, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack2);
            this.broadcastChanges();
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(context, player, STCMBlock.NEOFORGE_HAMMER.get());
    }
}
