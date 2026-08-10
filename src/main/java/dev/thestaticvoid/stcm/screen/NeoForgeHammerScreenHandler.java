package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.blocks.forgehammer.ForgeHammerRecipe;
import aztech.modern_industrialization.items.ForgeTool;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.*;

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
    private final Level level;

    private long lastSoundTime = 0;
    private final List<RecipeHolder<ForgeHammerRecipe>> availableRecipes;
    private final DataSlot selectedRecipe;
    private ItemStack inputStackCache = ItemStack.EMPTY, toolStackCache = ItemStack.EMPTY;

    public final Slot input;
    public final Slot tool;
    public final Slot output;

    public NeoForgeHammerScreenHandler(int containerId, Inventory playerInventory, FriendlyByteBuf dataContainer) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public NeoForgeHammerScreenHandler(int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(STCMMenuTypes.NEOFORGE_HAMMER_MENU.get(), containerId);

        this.container = container;
        this.data = containerData;
        this.level = playerInventory.player.level();

        this.availableRecipes = new ArrayList<>();
        this.selectedRecipe = DataSlot.standalone();

        this.input = this.addSlot(new Slot(container, SLOT_INPUT, 34, 33) {
            @Override
            public void setChanged() {
                super.setChanged();
                NeoForgeHammerScreenHandler.this.slotsChanged(this.container);
            }
        });
        this.tool = this.addSlot(new Slot(container, SLOT_TOOL, 8, 33) {
            @Override
            public void setChanged() {
                super.setChanged();
                NeoForgeHammerScreenHandler.this.slotsChanged(this.container);
            }
        });
        this.output = this.addSlot(new Slot(container, SLOT_OUTPUT, 143, 33) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
            }
        });



        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);

        this.addDataSlot(selectedRecipe);
        this.addDataSlots(containerData);
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

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public List<RecipeHolder<ForgeHammerRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    public void updateStatus() {
        this.inputStackCache = input.getItem().copy();
        this.toolStackCache = tool.getItem().copy();

        RecipeHolder<ForgeHammerRecipe> old = isInBounds(selectedRecipe.get()) ? availableRecipes.get(selectedRecipe.get()) : null;

        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.output.set(ItemStack.EMPTY);

        if (!input.getItem().isEmpty()) {
            Set<ItemVariant> outputs = new HashSet<>();

            var recipes = new ArrayList<>(this.level.getRecipeManager().getAllRecipesFor(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get()));
            // Process recipes with hammer damage first, duplicates will be filtered by output!
            recipes.sort(Comparator.comparing(h -> -h.value().hammerDamage()));

            for (var holder : recipes) {
                ForgeHammerRecipe recipe = holder.value();

                if (recipe.ingredient().test(input.getItem()) && recipe.count() <= input.getItem().getCount()) {
                    var output = ItemVariant.of(recipe.result());
                    if ((recipe.hammerDamage() != 0) && (!tool.getItem().isEmpty())) {
                        outputs.add(output);
                        availableRecipes.add(holder);
                    } else if (recipe.hammerDamage() == 0 && !outputs.contains(output)) {
                        outputs.add(output);
                        availableRecipes.add(holder);
                    }
                }
            }

            availableRecipes.sort(Comparator.comparing(RecipeHolder::id));

            for (int i = 0; i < availableRecipes.size(); i++) {
                if (old == availableRecipes.get(i)) {
                    this.selectedRecipe.set(i);
                    break;
                }
            }
            populateResult();
        }
    }

    void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<ForgeHammerRecipe> current = this.availableRecipes.get(getSelectedRecipe());
            if (current.value().hammerDamage() == 0
                    || (!tool.getItem().isEmpty() && tool.getItem().getDamageValue() < tool.getItem().getMaxDamage())) {
                this.output.set(current.value().result().copy());
            } else {
                this.output.set(ItemStack.EMPTY);
            }
        } else {
            this.output.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public void slotsChanged(Container container) {
        if (!ItemStack.matches(this.inputStackCache, input.getItem()) || !ItemStack.matches(this.toolStackCache, tool.getItem())) {
            updateStatus();
        }

        super.slotsChanged(container);
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        // Treat double-clicks on the output as two normal clicks instead of trying to "pick all"
        return slot.container != this.output.container && super.canTakeItemForPickAll(stack, slot);
    }
}
