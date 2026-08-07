package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.blocks.forgehammer.ForgeHammerRecipe;
import aztech.modern_industrialization.items.ForgeTool;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import dev.thestaticvoid.stcm.block.STCMBlock;
import dev.thestaticvoid.stcm.block.entity.NeoForgeHammerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.*;

public class NeoForgeHammerMenu extends AbstractContainerMenu {
    private final DataSlot selectedRecipe;
    private final List<RecipeHolder<ForgeHammerRecipe>> availableRecipes;

    private final ContainerLevelAccess context;
    private final Level level;
    private final Player player;

    private ItemStack inputStackCache = ItemStack.EMPTY;
    private ItemStack toolStackCache = ItemStack.EMPTY;

    public final NeoForgeHammerBlockEntity blockEntity;
    private final ContainerData data;

    public NeoForgeHammerMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public NeoForgeHammerMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data, ContainerLevelAccess context) {
        super(STCMMenuTypes.ENHANCED_FORGE_HAMMER_MENU.get(), containerId);
        this.blockEntity = ((NeoForgeHammerBlockEntity) entity);
        this.level = inventory.player.level();
        this.data = data;
        this.context = context;
        this.selectedRecipe = DataSlot.standalone();
        this.availableRecipes = new ArrayList<>();
        this.player = inventory.player;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 8, 33));
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 34, 33));
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 2, 143, 33));

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

    public int getAvailableRecipeCount() {
        return availableRecipes.size();
    }

    public int getSelectedRecipe() {
        return selectedRecipe.get();
    }

    public List<RecipeHolder<ForgeHammerRecipe>> getAvailableRecipes() {
        return availableRecipes;
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
        return stillValid(context, player, STCMBlock.ENHANCED_FORGE_HAMMER.get());
    }

    @Override
    public void slotsChanged(Container container) {
        if (!ItemStack.matches(this.inputStackCache, this.slots.get(36).getItem()) || !ItemStack.matches(this.toolStackCache, this.slots.get(37).getItem())) {
            updateStatus();
        }

        super.slotsChanged(container);
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    public void updateStatus() {
        this.inputStackCache = getInputSlotItem().copy();
        this.toolStackCache = getToolSlotItem().copy();

        RecipeHolder<ForgeHammerRecipe> old = isInBounds(selectedRecipe.get()) ? availableRecipes.get(selectedRecipe.get()) : null;

        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.slots.get(38).set(ItemStack.EMPTY);

        if (!getInputSlotItem().isEmpty()) {
            Set<ItemVariant> outputs = new HashSet<>();

            var recipes = new ArrayList<>(this.level.getRecipeManager().getAllRecipesFor(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get()));
            recipes.sort(Comparator.comparing(h -> -h.value().hammerDamage()));

            for (var holder : recipes) {
                ForgeHammerRecipe recipe = holder.value();

                if (recipe.ingredient().test(getInputSlotItem()) && recipe.count() <= getInputSlotItem().getCount()) {
                    var output = ItemVariant.of(recipe.result());
                    if ((recipe.hammerDamage() != 0) && (!getToolSlotItem().isEmpty())) {
                        outputs.add(output);
                        availableRecipes.add(holder);
                    } else if (recipe.hammerDamage() == 0 && !outputs.contains(output)) {
                        outputs.add(output);
                        availableRecipes.add(holder);
                    }
                }
            }

            availableRecipes.sort(Comparator.comparing(RecipeHolder::id));

            for (int i = 0; i < availableRecipes.size(); ++i) {
                if (old == availableRecipes.get(i)) {
                    this.selectedRecipe.set(i);
                    break;
                }
            }
            populateResult();
        }
    }

    private void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<ForgeHammerRecipe> current = this.availableRecipes.get(getSelectedRecipe());
            if (current.value().hammerDamage() == 0 || (!getToolSlotItem().isEmpty() && getToolSlotItem().getDamageValue() < getToolSlotItem().getMaxDamage())) {
                this.slots.get(38).set(current.value().result().copy());
            } else {
                this.slots.get(38).set(ItemStack.EMPTY);
            }
        } else {
            this.slots.get(38).set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }



    private void onCraft() {
        RecipeHolder<ForgeHammerRecipe> current = this.availableRecipes.get(this.selectedRecipe.get());
        getInputSlotItem().shrink(current.value().count());
        if (!getToolSlotItem().isEmpty()) {
            if (!level.isClientSide()) {
                getToolSlotItem().hurtAndBreak(current.value().hammerDamage(), (ServerLevel) level, (ServerPlayer) this.player,
                        item -> this.slots.get(36).set(ItemStack.EMPTY));
            }
            if (getToolSlotItem().getDamageValue() >= getToolSlotItem().getMaxDamage()) {
                this.slots.get(36).set(ItemStack.EMPTY);

                context.execute((world, pos) -> {
                    world.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                });
            }

        } else if (current.value().hammerDamage() > 0) {
            throw new IllegalStateException("Forge Hammer Exception : Tool crafting without a tool");
        }

        this.updateStatus();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        // Treat double-clicks on the output as two normal clicks instead of trying to "pick all"
        return slot.container != this.slots.get(38).container && super.canTakeItemForPickAll(stack, slot);
    }

    public void moveRecipe(ResourceLocation recipeId, int fillAction, int amount) {
        var recipeHolder = this.level.getRecipeManager().getAllRecipesFor(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get()).stream()
                .filter(r -> r.id().equals(recipeId)).findFirst().orElse(null);
        if (recipeHolder == null) {
            return;
        }

        var recipe = recipeHolder.value();
        boolean firstPass = true;

        while (amount > 0) {
            boolean didSomething = false;

            if (recipe.ingredient().test(getInputSlotItem())) {
                // Pull from player inventory
                int targetAmount = firstPass ? recipe.count() : getInputSlotItem().getCount() + recipe.count();
                int delta = targetAmount - getInputSlotItem().getCount();
                if (delta < 0) {
                    player.getInventory().placeItemBackInInventory(this.slots.get(37).remove(-delta));
                    didSomething = true;
                } else {
                    int toPull = delta;
                    for (int i = 0; i < 36; ++i) {
                        Slot slot = this.slots.get(i);
                        if (ItemStack.isSameItemSameComponents(slot.getItem(), getInputSlotItem())) {
                            int toMove = Math.min(toPull, this.slots.get(37).getMaxStackSize(getInputSlotItem()) - getInputSlotItem().getCount());
                            if (toMove > 0) {
                                ItemStack removed = slot.remove(toMove);
                                getInputSlotItem().grow(removed.getCount());
                                this.slots.get(37).setChanged();
                                toPull -= removed.getCount();
                                didSomething = true;
                            }
                        }
                    }
                }
            } else {
                // Remove old input
                var oldInput = this.slots.get(37).remove(getInputSlotItem().getCount());
                player.getInventory().placeItemBackInInventory(oldInput);
                // Find matching stack
                var matchingStack = ItemStack.EMPTY;
                for (int i = 0; i < 36 && matchingStack.isEmpty(); ++i) {
                    Slot slot = this.slots.get(i);
                    if (recipe.ingredient().test(slot.getItem())) {
                        matchingStack = slot.getItem().copy();
                    }
                }
                if (matchingStack.isEmpty()) {
                    return;
                }
                // Pull matching input from player inventory
                int toPull = recipe.count();
                this.slots.get(37).set(matchingStack.copy());
                getInputSlotItem().setCount(0);
                for (int i = 0; i < 36; ++i) {
                    Slot slot = this.slots.get(i);
                    if (ItemStack.isSameItemSameComponents(slot.getItem(), matchingStack)) {
                        int toMove = Math.min(toPull, this.slots.get(37).getMaxStackSize(getInputSlotItem()) - getInputSlotItem().getCount());
                        if (toMove > 0) {
                            ItemStack removed = slot.remove(toMove);
                            getInputSlotItem().grow(removed.getCount());
                            this.slots.get(37).setChanged();
                            toPull -= removed.getCount();
                            didSomething = true;
                        }
                    }
                }
            }

            // Move hammer into gui
            if (recipe.hammerDamage() > 0 && !this.slots.get(36).hasItem()) {
                for (int i = 0; i < 36; ++i) {
                    Slot slot = this.slots.get(i);
                    if (slot.getItem().is(ForgeTool.TAG)) {
                        this.slots.get(36).set(slot.remove(1));
                        didSomething = true;
                        break;
                    }
                }
            }

            // Select recipe
            int recipeIndex = -1;
            for (int i = 0; i < this.availableRecipes.size(); ++i) {
                if (this.availableRecipes.get(i).id().equals(recipeId)) {
                    recipeIndex = i;
                    break;
                }
            }
            if (recipeIndex == -1) {
                return;
            }
            if (selectedRecipe.get() != recipeIndex) {
                selectedRecipe.set(recipeIndex);
                didSomething = true;
            }
            this.populateResult();

            // Process fill action
            ItemStack oldOutput = this.slots.get(38).getItem().copy();
            switch (fillAction) {
                case 1 -> clicked(this.slots.get(38).index, 0, ClickType.PICKUP, player);
                case 2 -> clicked(this.slots.get(38).index, 0, ClickType.QUICK_MOVE, player);
            }
            if (!ItemStack.matches(oldOutput, this.slots.get(38).getItem())) {
                didSomething = true;
            }

            amount--;
            if (!didSomething && !firstPass) {
                break;
            }
            firstPass = false;
        }
    }

    private ItemStack getToolSlotItem() {
        return this.slots.get(36).getItem();
    }

    private ItemStack getInputSlotItem() {
        return this.slots.get(37).getItem();
    }

    private ItemStack getOutputSlotItem() {
        return this.slots.get(38).getItem();
    }
}