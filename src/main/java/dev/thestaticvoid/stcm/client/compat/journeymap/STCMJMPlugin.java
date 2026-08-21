package dev.thestaticvoid.stcm.client.compat.journeymap;

import dev.thestaticvoid.stcm.STCM;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.awt.*;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class STCMJMPlugin implements IClientPlugin {
    private static STCMJMPlugin INSTANCE;

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

    public static Waypoint createOreSampleWaypoint(BlockPos position, Level level, int color, String name) {
        Waypoint waypoint = null;
        try {
            ResourceLocation icon = getBlockTextureResourceLocation(level.getBlockState(position));

            waypoint = WaypointFactory.createWaypoint(STCM.ID, position, level.dimension(), true);
            waypoint.setName(name);
            waypoint.setIconResourceLoctaion(icon);
            waypoint.setIconColor(0xffffff);
            waypoint.setBeaconColor(color);
            waypoint.setLabelColor(color);
            INSTANCE.jmAPI.addWaypoint(STCM.ID, waypoint);
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
        boolean isSuccess = true;
        var waypoints = INSTANCE.jmAPI.getAllWaypoints(dimension);
        for (var waypoint : waypoints) {
            if(waypoint.getBlockPos().closerThan(position, 3) && waypoint.getName().equals(name)) {
                isSuccess = false;
            }
        }
        return isSuccess;
    }
}
