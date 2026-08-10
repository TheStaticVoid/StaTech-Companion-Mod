package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.items.ForgeTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class NeoForgeHammerScreenHandler extends AbstractContainerMenu {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_TOOL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_COUNT = 3;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final ContainerData data;
    private final Container container;

    public NeoForgeHammerScreenHandler(int containerId, Inventory playerInventory, FriendlyByteBuf dataContainer) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public NeoForgeHammerScreenHandler(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(STCMMenuTypes.NEOFORGE_HAMMER_MENU.get(), containerId);

        this.container = container;
        this.data = containerData;

        this.addSlot(new Slot(container, SLOT_INPUT, 34, 33));
        this.addSlot(new Slot(container, SLOT_TOOL, 8, 33));
        this.addSlot(new Slot(container, SLOT_OUTPUT, 143, 33));

        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();

            if (index == SLOT_OUTPUT) {
                if (!this.moveItemStackTo(itemStack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack1, itemStack);
            } else if (index != SLOT_INPUT && index != SLOT_TOOL) {
                if (itemStack1.is(ForgeTool.TAG)) {
                    if (!this.moveItemStackTo(itemStack1, SLOT_TOOL, SLOT_TOOL + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(itemStack1, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemStack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
