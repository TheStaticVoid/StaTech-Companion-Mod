package dev.thestaticvoid.stcm.block.entity;

import aztech.modern_industrialization.MIRegistries;
import aztech.modern_industrialization.blocks.forgehammer.ForgeHammerRecipe;
import aztech.modern_industrialization.items.ForgeTool;
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreenHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class NeoForgeHammerBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SLOT_TOOL = 0;
    private static final int SLOT_INPUT = 1;
    private static final int SLOT_OUTPUT = 2;
    private ItemStack inputStackCache;
    private ItemStack toolStackCache;
    protected final ContainerData data;

    private final DataSlot selectedRecipe;
    private final List<RecipeHolder<ForgeHammerRecipe>> availableRecipes;
    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                slotsChanged(slot);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch(slot) {
                case SLOT_TOOL -> stack.is(ForgeTool.TAG);
                case SLOT_OUTPUT -> false;
                default -> true;
            };
        }
    };


    public NeoForgeHammerBlockEntity(BlockPos pos, BlockState blockState) {
        super(STCMBlockEntity.NEOFORGE_HAMMER_BE.get(), pos, blockState);
        this.data = new ContainerData() {
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
        this.inputStackCache = itemHandler.getStackInSlot(SLOT_INPUT);
        this.toolStackCache = itemHandler.getStackInSlot(SLOT_TOOL);
        this.selectedRecipe = DataSlot.standalone();
        this.availableRecipes = new ArrayList<>();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.stcm.neoforge_hammer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new NeoForgeHammerScreenHandler(i, inventory, this, data, ContainerLevelAccess.create(level, getBlockPos()));
    }

    public void dropInventory() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public int getSelectedRecipe() {
        return selectedRecipe.get();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }

    public void slotsChanged(int slot) {
        if (slot == SLOT_TOOL && !ItemStack.matches(itemHandler.getStackInSlot(slot), toolStackCache)) {
            if (!ItemStack.matches(itemHandler.getStackInSlot(slot), toolStackCache)) {
                toolStackCache = itemHandler.getStackInSlot(slot).copy();
                updateStatus();
            }
        } else if (slot == SLOT_INPUT) {
            if (!ItemStack.matches(itemHandler.getStackInSlot(slot), inputStackCache)) {
                inputStackCache = itemHandler.getStackInSlot(slot).copy();
                updateStatus();
            }
        } else if (slot == SLOT_OUTPUT){
            onCraft();
        } else {

        }
    }

    public void updateStatus() {
        RecipeHolder<ForgeHammerRecipe> old = isInBounds(selectedRecipe.get()) ? availableRecipes.get(selectedRecipe.get()) : null;
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        ItemStack input = this.getInputSlot();
        ItemStack tool = this.getToolSlot();
        this.setOutputSlot(ItemStack.EMPTY);

        if (input.isEmpty()) {
            Set<ItemVariant> outputs = new HashSet<>();

            var recipes = new ArrayList<>(this.level.getRecipeManager().getAllRecipesFor(MIRegistries.FORGE_HAMMER_RECIPE_TYPE.get()));
            recipes.sort(Comparator.comparing(h -> -h.value().hammerDamage()));

            for (var holder : recipes) {
                ForgeHammerRecipe recipe = holder.value();

                if (recipe.ingredient().test(input) && recipe.count() <= input.getCount()) {
                    var output = ItemVariant.of(recipe.result());
                    if ((recipe.hammerDamage() != 0) && (!tool.isEmpty())) {
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

    private void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<ForgeHammerRecipe> current = this.availableRecipes.get(getSelectedRecipe());
            if (current.value().hammerDamage() == 0
                    || (!getToolSlot().isEmpty() && getToolSlot().getDamageValue() < getToolSlot().getMaxDamage())) {
                this.setOutputSlot(current.value().result().copy());
            } else {
                this.setOutputSlot(ItemStack.EMPTY);
            }
        } else {
            this.setOutputSlot(ItemStack.EMPTY);
        }
    }

    private void onCraft() {
        RecipeHolder<ForgeHammerRecipe> current = this.availableRecipes.get(this.selectedRecipe.get());
        getInputSlot().shrink(current.value().count());
        if (!getToolSlot().isEmpty()) {
            if (!level.isClientSide()) {
                getToolSlot().hurtAndBreak(current.value().hammerDamage(), (ServerLevel) level, null,
                        item -> setToolSlot(ItemStack.EMPTY));
            }
            if (getToolSlot().getDamageValue() >= getToolSlot().getMaxDamage()) {
                setToolSlot(ItemStack.EMPTY);

                level.playSound(null, getBlockPos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (current.value().hammerDamage() > 0) {
            throw new IllegalStateException("NeoForge Hammer Exception : Tool crafting without a tool");
        }

        this.updateStatus();
    }

    private ItemStack getToolSlot() {
        return this.itemHandler.getStackInSlot(SLOT_TOOL);
    }

    private void setToolSlot(ItemStack stack) {
        this.itemHandler.setStackInSlot(SLOT_TOOL, stack);
    }

    private ItemStack getInputSlot() {
        return this.itemHandler.getStackInSlot(SLOT_INPUT);
    }

    private void setInputSlot(ItemStack stack) {
        this.itemHandler.setStackInSlot(SLOT_INPUT, stack);
    }

    private void setOutputSlot(ItemStack stack) {
        this.itemHandler.setStackInSlot(SLOT_OUTPUT, stack);
    }

    public void clearOutputSlot() {
        this.setOutputSlot(ItemStack.EMPTY);
    }

    public int getAvailableRecipeCount() {
        return availableRecipes.size();
    }

    public List<RecipeHolder<ForgeHammerRecipe>> getAvailableRecipes() {
        return availableRecipes;
    }
}
