package dev.thestaticvoid.stcm.item;

import dev.thestaticvoid.stcm.STCMConfig;
import dev.thestaticvoid.stcm.client.compat.journeymap.STCMJMPlugin;
import journeymap.api.v2.common.waypoint.Waypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.*;

public class ProspectorPick extends Item {
    private static final Map<String, Integer> MaterialMap = new HashMap<>();
    private final int PICK_COOLDOWN = 100; // 5 seconds
    private long lastPickUseTime = 0;
    private Level level;
    private Player player;
    private Map<BlockPos, BlockState> oresFound = new HashMap<>();
    private List<DepositInfo> depositsFound = new ArrayList<>();

    public ProspectorPick(Properties properties) {
        super(properties);
        populateMaterialMap();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        level = context.getLevel();
        player = context.getPlayer();

        if (player == null) {
            return super.useOn(context);
        }

        Block targetedBlock = level.getBlockState(context.getClickedPos()).getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(targetedBlock);

        if (id.getNamespace().equals("kubejs") && id.getPath().contains("ore_sample")) {
            if (level.isClientSide()) {
                String material = id.getPath().substring(0, id.getPath().indexOf("_ore"));
                int color = ChatFormatting.getByName("white").getColor();
                if (MaterialMap.containsKey(material)) {
                    color = MaterialMap.get(material);
                }

                String formattedMatName = material.toUpperCase().charAt(0) + material.substring(1);
                String formattedPosition = String.format("(%s, %s, %s)", context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());

                if (STCMJMPlugin.proximityCheck(context.getClickedPos(), level.dimension(), formattedMatName)) {
                    Waypoint waypoint = STCMJMPlugin.createOreSampleWaypoint(context.getClickedPos(), level, color, formattedMatName, player.isCrouching());

                    if (waypoint != null) {
                        player.displayClientMessage(Component.translatable("chat.stcm.waypoint_success", formattedMatName, formattedPosition), true);
                        level.playSound(player, context.getClickedPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else {
                        player.displayClientMessage(Component.translatable("chat.stcm.waypoint_failed"), true);
                        return InteractionResult.CONSUME;
                    }
                } else {
                    if (player.isCrouching()) {
                        if (STCMJMPlugin.isShownInWorld(context.getClickedPos(), level.dimension(), formattedMatName)) {
                            STCMJMPlugin.showInWorld(context.getClickedPos(), level.dimension(), formattedMatName, false);
                            player.displayClientMessage(Component.translatable("chat.stcm.waypoint_updated_hide_in_world"), true);

                        } else {
                            STCMJMPlugin.showInWorld(context.getClickedPos(), level.dimension(), formattedMatName, true);
                            player.displayClientMessage(Component.translatable("chat.stcm.waypoint_updated_show_in_world"), true);
                        }
                        level.playSound(player, context.getClickedPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    } else {
                        player.displayClientMessage(Component.translatable("chat.stcm.waypoint_proximity_fail", formattedMatName), true);
                    }
                }
            }
        } else if (!level.isClientSide() && !player.isCrouching()) {
            if (level.getGameTime() > lastPickUseTime + PICK_COOLDOWN) {
                lastPickUseTime = level.getGameTime();
                checkBlocksInArea(context.getClickedPos(), level);

                if (!this.depositsFound.isEmpty()) {
                    player.sendSystemMessage(Component.translatable("chat.stcm.prospector_success"));
                    for (DepositInfo info : this.depositsFound) {
                        player.sendSystemMessage(Component.translatable("chat.stcm.prospector_deposit_info", info.getBlockState().getBlock().getName(), info.getDistance(context.getClickedPos())));
                    }
                } else {
                    player.sendSystemMessage(Component.translatable("chat.stcm.prospector_no_deposits"));
                }

                level.playSound(null, context.getClickedPos(), SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                return InteractionResult.SUCCESS;
            } else {
                player.displayClientMessage(Component.translatable("chat.stcm.prospector_cooldown", ((lastPickUseTime + PICK_COOLDOWN - level.getGameTime()) / 20.0)), true);
                return super.useOn(context);
            }
        }
        return super.useOn(context);
    }

    // This being hardcoded is pretty bad but idk i'm lazy
    private void populateMaterialMap() {
        MaterialMap.put("bauxite", ChatFormatting.getByName("gold").getColor());
        MaterialMap.put("salt", ChatFormatting.getByName("gray").getColor());
        MaterialMap.put("titanium", ChatFormatting.getByName("light_purple").getColor());
        MaterialMap.put("uranium", ChatFormatting.getByName("green").getColor());
        MaterialMap.put("lead", ChatFormatting.getByName("dark_blue").getColor());
        MaterialMap.put("nickel", ChatFormatting.getByName("white").getColor());
        MaterialMap.put("platinum", ChatFormatting.getByName("white").getColor());
        MaterialMap.put("tin", ChatFormatting.getByName("gray").getColor());
        MaterialMap.put("coal", ChatFormatting.getByName("black").getColor());
        MaterialMap.put("copper", ChatFormatting.getByName("red").getColor());
        MaterialMap.put("diamond", ChatFormatting.getByName("aqua").getColor());
        MaterialMap.put("emerald", ChatFormatting.getByName("green").getColor());
        MaterialMap.put("gold", ChatFormatting.getByName("yellow").getColor());
        MaterialMap.put("iron", ChatFormatting.getByName("gray").getColor());
        MaterialMap.put("lapis", ChatFormatting.getByName("blue").getColor());
        MaterialMap.put("nether_gold", ChatFormatting.getByName("yellow").getColor());
        MaterialMap.put("nether_quartz", ChatFormatting.getByName("gray").getColor());
        MaterialMap.put("quartz", ChatFormatting.getByName("gray").getColor());
        MaterialMap.put("redstone", ChatFormatting.getByName("dark_red").getColor());
        MaterialMap.put("zinc", ChatFormatting.getByName("gray").getColor());
    }

    private void checkBlocksInArea(BlockPos startPosition, Level level) {
        oresFound.clear();
        depositsFound.clear();

        // Thank you Mojang, very cool
        Iterable<BlockPos> blockPosIterator = BlockPos.withinManhattan(
                startPosition,
                STCMConfig.CONFIG.prospectorHorizontalRange.get(),
                STCMConfig.CONFIG.prospectorVerticalRange.get(),
                STCMConfig.CONFIG.prospectorHorizontalRange.get());
        for (BlockPos pos : blockPosIterator) {
            BlockState state = level.getBlockState(pos);

            if (state.is(Tags.Blocks.ORES)) {
                // The Iterator returns MutableBlockPos which was causing issues
                this.oresFound.put(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), state);
            }
        }

        Set<BlockState> depositTypes = new HashSet<>();
        for (BlockPos pos : this.oresFound.keySet()) {
            if (depositTypes.contains(this.oresFound.get(pos))) {
                continue;
            }

            List<BlockPos> scannedPos = new ArrayList<>();
            int size = countNeighbors(this.oresFound.get(pos).getBlock(), pos, STCMConfig.CONFIG.prospectorMinDepositSize.get(), scannedPos);
            if (size >= STCMConfig.CONFIG.prospectorMinDepositSize.get()) {
                this.depositsFound.add(new DepositInfo(pos, this.oresFound.get(pos), size));
                depositTypes.add(this.oresFound.get(pos));
            }
        }
    }

    private int countNeighbors(Block blockType, BlockPos pos, int maxCount, List<BlockPos> scannedPos) {
        int count = 1;
        scannedPos.add(pos);

        for (Direction direction : Direction.values()) {
            if (count >= maxCount) {
                break;
            }

            BlockPos adjPos = pos.relative(direction);
            if (this.level.getBlockState(adjPos).is(blockType) && !scannedPos.contains(adjPos)) {
                count += this.countNeighbors(blockType, adjPos, maxCount - count, scannedPos);
            }
        }

        return count;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.stcm.prospector_tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.stcm.prospector_tooltip_shift",
                    STCMConfig.CONFIG.prospectorHorizontalRange.get(),
                    STCMConfig.CONFIG.prospectorVerticalRange.get()));
        }
    }

    private static class DepositInfo {
        private BlockPos position;
        private BlockState blockState;
        private int count;

        public DepositInfo(BlockPos position, BlockState blockState, int count) {
            this.position = position;
            this.blockState = blockState;
            this.count = count;
        }

        public BlockPos getPosition() {
            return position;
        }

        public String getPositionString() {
            return String.format("(%s, %s, %s)", this.position.getX(), this.position.getY(), this.position.getZ());
        }

        public int getDistance(BlockPos pos) {
            return (int) Math.sqrt(pos.distSqr(this.position));
        }

        public void setPosition(BlockPos position) {
            this.position = position;
        }

        public BlockState getBlockState() {
            return blockState;
        }

        public void setBlockState(BlockState blockState) {
            this.blockState = blockState;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
