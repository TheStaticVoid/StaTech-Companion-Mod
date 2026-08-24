package dev.thestaticvoid.stcm.item;

import dev.thestaticvoid.stcm.STCMConfig;
import dev.thestaticvoid.stcm.client.compat.journeymap.STCMJMPlugin;
import dev.thestaticvoid.stcm.data.MaterialLoader;
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
    private final Map<BlockState, BlockPos> depositsFound = new HashMap<>();

    public ProspectorPick(Properties properties) {
        super(properties);
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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        level = context.getLevel();
        Player player = context.getPlayer();

        if (level.isClientSide()) {
            return doSampleInteraction(level, player, context);
        } else {
            if (!player.isCrouching()) {
                return doDepositScan(level, player, context);
            }
        }
        return super.useOn(context);
    }

    private InteractionResult doSampleInteraction(Level level, Player player, UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Block targetedBlock = level.getBlockState(blockPos).getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(targetedBlock);

        if (id.getNamespace().equals("kubejs") && id.getPath().contains("ore_sample")) {
            String material = id.getPath().substring(0, id.getPath().indexOf("_ore"));
            ChatFormatting waypointColor = ChatFormatting.getByName(MaterialLoader.get(material));
            if (waypointColor == null) {
                waypointColor = ChatFormatting.WHITE;
            }

            String formattedMatName = capitalizeFirstLetter(material);
            String formattedPosition = String.format("(%s, %s, %s)", blockPos.getX(), blockPos.getY(), blockPos.getZ());

            // Check to make sure there are no other waypoints of the same material type in the vicinity
            if (STCMJMPlugin.proximityCheck(blockPos, level.dimension(), formattedMatName)) {
                Waypoint waypoint = STCMJMPlugin.createOreSampleWaypoint(
                        blockPos,
                        level,
                        waypointColor.getColor(),
                        formattedMatName,
                        player.isCrouching());

                if (waypoint != null) {
                    player.displayClientMessage(Component.translatable("chat.stcm.waypoint_success", formattedMatName, formattedPosition), true);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(Component.translatable("chat.stcm.waypoint_failed"), true);
                    return InteractionResult.CONSUME;
                }
            } else {
                // Otherwise need to check if it's an inworld waypoint toggle
                if (player.isCrouching()) {
                    if (STCMJMPlugin.isShownInWorld(blockPos, level.dimension(), formattedMatName)) {
                        STCMJMPlugin.showInWorld(blockPos, level.dimension(), formattedMatName, false);
                        player.displayClientMessage(Component.translatable("chat.stcm.waypoint_updated_hide_in_world"), true);
                    } else {
                        STCMJMPlugin.showInWorld(blockPos, level.dimension(), formattedMatName, true);
                        player.displayClientMessage(Component.translatable("chat.stcm.waypoint_updated_show_in_world"), true);
                    }
                    level.playSound(player, blockPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(Component.translatable("chat.stcm.waypoint_proximity_fail", formattedMatName), true);
                }
            }
        }
        return super.useOn(context);
    }

    private InteractionResult doDepositScan(Level level, Player player, UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        if (level.getGameTime() > lastPickUseTime + PICK_COOLDOWN) {
            lastPickUseTime = level.getGameTime();
            checkBlocksInArea(blockPos, level);

            if (!this.depositsFound.isEmpty()) {
                player.sendSystemMessage(Component.translatable("chat.stcm.prospector_success"));

                Map<String, BlockPos> oreNameMap = new HashMap<>();
                this.depositsFound.forEach((state, pos) -> {
                    String oreName = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
                    if (oreName.contains("deepslate_")) {
                        oreName = oreName.substring("deepslate_".length());
                    }
                    oreNameMap.put(capitalizeFirstLetter((oreName.substring(0, oreName.indexOf("_ore"))).replace("_", " ")), pos);
                });

                SortedSet<String> sortedKeys = new TreeSet<>(oreNameMap.keySet());
                for (String key : sortedKeys) {
                    int distance = (int) Math.sqrt(oreNameMap.get(key).distSqr(blockPos));
                    player.sendSystemMessage(Component.translatable("chat.stcm.prospector_deposit_info", key, distance));
                }
            } else {
                player.sendSystemMessage(Component.translatable("chat.stcm.prospector_no_deposits"));
            }

            level.playSound(null, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(Component.translatable("chat.stcm.prospector_cooldown", ((lastPickUseTime + PICK_COOLDOWN - level.getGameTime()) / 20.0)), true);
            return super.useOn(context);
        }
    }

    private void checkBlocksInArea(BlockPos startPosition, Level level) {
        Map<BlockPos, BlockState> oresFound = new HashMap<>();
        this.depositsFound.clear();

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
                oresFound.put(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), state);
            }
        }

        Set<BlockState> depositTypes = new HashSet<>();
        oresFound.forEach((pos, state) -> {
            ResourceLocation temporary = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            Block ore = null, deepslate = null;
            BlockState defaultState = null;
            if (temporary.getPath().contains("deepslate_")) {
                String formattedPath = temporary.getPath().substring("deepslate_".length());
                if (BuiltInRegistries.BLOCK.containsKey(ResourceLocation.fromNamespaceAndPath(temporary.getNamespace(), formattedPath))) {
                    ore = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(temporary.getNamespace(), formattedPath));
                }
                deepslate = state.getBlock();
                defaultState = ore == null ? state : ore.defaultBlockState();
            } else {
                String formattedPath = "deepslate_" + temporary.getPath();
                if (BuiltInRegistries.BLOCK.containsKey(ResourceLocation.fromNamespaceAndPath(temporary.getNamespace(), formattedPath))) {
                    deepslate = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(temporary.getNamespace(), formattedPath));
                }
                ore = state.getBlock();
                defaultState = state;
            }

            if (!depositTypes.contains(defaultState)) {
                List<BlockPos> scannedPos = new ArrayList<>();
                int size = countNeighbors(ore, deepslate, pos, STCMConfig.CONFIG.prospectorMinDepositSize.get(), scannedPos);
                if (size >= STCMConfig.CONFIG.prospectorMinDepositSize.get()) {
                    this.depositsFound.put(defaultState, pos);
                    depositTypes.add(defaultState);
                }
            }
        });
    }

    private int countNeighbors(Block oreBlock, Block deepslateBlock, BlockPos pos, int maxCount, List<BlockPos> scannedPos) {
        int count = 1;
        scannedPos.add(pos);

        for (Direction direction : Direction.values()) {
            if (count >= maxCount) {
                break;
            }

            BlockPos adjPos = pos.relative(direction);
            if ((this.level.getBlockState(adjPos).is(oreBlock) || this.level.getBlockState(adjPos).is(deepslateBlock)) && !scannedPos.contains(adjPos)) {
                count += this.countNeighbors(oreBlock, deepslateBlock, adjPos, maxCount - count, scannedPos);
            }
        }

        return count;
    }

    private String  capitalizeFirstLetter(String word) {
        String[] separated = word.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < separated.length; i++) {
            if (separated[i].length() > 1) {
                formatted.append(separated[i].toUpperCase().charAt(0)).append(separated[i].substring(1)).append(" ");
            }
        }

        return formatted.toString().trim();
    }
}
