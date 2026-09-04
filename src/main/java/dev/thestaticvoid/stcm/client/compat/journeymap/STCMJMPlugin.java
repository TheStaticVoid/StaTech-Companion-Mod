package dev.thestaticvoid.stcm.client.compat.journeymap;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.STCMConfig;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class STCMJMPlugin implements IClientPlugin {
    private static STCMJMPlugin INSTANCE;
    private static final String GROUP_NAME = "Deposits";

    private IClientAPI jmAPI;

    public STCMJMPlugin() {
        INSTANCE = this;
    }

    public static STCMJMPlugin getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public String getModId() {
        return STCM.ID;
    }

    @Override
    public void initialize(IClientAPI jmClientApi) {
        this.jmAPI = jmClientApi;
    }

    public static Waypoint createOreSampleWaypoint(BlockPos position, Level level, int color, String name, boolean showInWorld) {
        Waypoint waypoint = null;
        try {
            ResourceLocation icon = getBlockTextureResourceLocation(level.getBlockState(position));

            waypoint = WaypointFactory.createWaypoint(STCM.ID, position, level.dimension(), true);
            waypoint.setName(name);
            waypoint.setIconResourceLoctaion(icon);
            waypoint.setIconColor(0xffffff);
            waypoint.setBeaconColor(color);
            waypoint.setLabelColor(color);
            waypoint.setShowInWorld(showInWorld);
            waypoint.setIconTextureSize(16, 16);
            INSTANCE.jmAPI.addWaypoint(STCM.ID, waypoint);

            WaypointGroup wg = getModWaypointGroup();
            if (wg != null) {
                wg.setLocked(false);
                wg.addWaypoint(waypoint);
                wg.setLocked(true);
            }
        } catch (Throwable t) {
            STCM.LOGGER.error(t.getMessage(), t);
            return null;
        }

        return waypoint;
    }

    private static ResourceLocation getBlockTextureResourceLocation(BlockState state) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        ResourceLocation sprite = model.getParticleIcon(ModelData.EMPTY).contents().name();
        return ResourceLocation.fromNamespaceAndPath(sprite.getNamespace(), "textures/" + sprite.getPath() + ".png");
    }

    public static Boolean proximityCheck(BlockPos position, ResourceKey<Level> dimension, String name) {
        return getProximityWaypoint(position, dimension, name) == null;
    }

    public static Boolean isShownInWorld(BlockPos position, ResourceKey<Level> dimension, String name) {
        return getProximityWaypoint(position, dimension, name).showInWorld();
    }

    public static void showInWorld(BlockPos position, ResourceKey<Level> dimension, String name, boolean shouldShow) {
        Waypoint wp = getProximityWaypoint(position, dimension, name);
        wp.setShowInWorld(shouldShow);
    }

    private static Waypoint getProximityWaypoint(BlockPos position, ResourceKey<Level> dimension, String name) {
        var waypoints = INSTANCE.jmAPI.getAllWaypoints(dimension);
        Waypoint waypointToFind = null;
        for (var waypoint : waypoints) {
            if (waypoint.getBlockPos().closerThan(position, STCMConfig.CONFIG.sampleProximityRange.get()) && waypoint.getName().equals(name)) {
                waypointToFind = waypoint;
            }
        }

        return waypointToFind;
    }

    private static WaypointGroup getModWaypointGroup() {
        WaypointGroup group = null;

        if (INSTANCE.jmAPI.getWaypointGroupByName(STCM.ID, GROUP_NAME) == null) {
            WaypointGroup wg = WaypointFactory.createWaypointGroup(STCM.ID, GROUP_NAME);
            wg.setLocked(true);
            INSTANCE.jmAPI.addWaypointGroup(wg);
            group = wg;
        } else {
            group = INSTANCE.jmAPI.getWaypointGroupByName(STCM.ID, GROUP_NAME);
        }
        return group;
    }
}
