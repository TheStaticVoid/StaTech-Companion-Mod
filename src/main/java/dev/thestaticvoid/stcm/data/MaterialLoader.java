package dev.thestaticvoid.stcm.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import dev.thestaticvoid.stcm.STCM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

import java.util.HashMap;
import java.util.Map;

public class MaterialLoader extends SimpleJsonResourceReloadListener {

    public static final String ID = "material_colors";
    public static final MaterialLoader INSTANCE = new MaterialLoader();

    private static final Map<String, String> DATA = new HashMap<>();

    private MaterialLoader() {
        super(new Gson(), ID);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DATA.clear();

        ConditionalOps<JsonElement> ops = makeConditionalOps();
        resourceLocationJsonElementMap.forEach((location, element) -> {
            DataResult<MaterialColor> data = MaterialColor.CODEC.parse(ops, element);
            if (data.error().isPresent()) {
                STCM.LOGGER.error("[Material Color Data] Error loading entry [{}]: {}", location, data.error().get());
                return;
            }

            if (data.result().isPresent()) {
                MaterialColor matColor = data.getOrThrow();
                DATA.put(matColor.name(), matColor.color());
            }
        });
    }

    public static String get(String materialName) {
        return DATA.getOrDefault(materialName, "white");
    }
}
