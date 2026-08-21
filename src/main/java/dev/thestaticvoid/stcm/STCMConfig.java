package dev.thestaticvoid.stcm;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class STCMConfig {
    public static final STCMConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<STCMConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(STCMConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<Integer> lootrMinXp;
    public final ModConfigSpec.ConfigValue<Integer> lootrMaxXp;
    public final ModConfigSpec.ConfigValue<Boolean> nukeBlockDamage;
    public final ModConfigSpec.ConfigValue<Integer> prospectorHorizontalRange;
    public final ModConfigSpec.ConfigValue<Integer> prospectorVerticalRange;
    public final ModConfigSpec.ConfigValue<Integer> prospectorMinDepositSize;
    public final ModConfigSpec.ConfigValue<Integer> sampleProximityRange;

    private STCMConfig(ModConfigSpec.Builder builder){
        lootrMinXp = builder.comment("The minimum amount of XP gained from opening Lootr chests.").define("lootr_min_xp", 15);
        lootrMaxXp = builder.comment("The maximum amount of XP gained from opening Lootr chests.").define("lootr_max_xp", 20);
        nukeBlockDamage = builder.comment("Should nuke explosions do block damage?").define("nuke_damage", true);
        prospectorHorizontalRange = builder.comment("The horizontal radius for the prospector to scan").define("prospector_x_radius", 16);
        prospectorVerticalRange = builder.comment("The vertical radius for the prospector to scan").define("prospector_y_radius", 256);
        prospectorMinDepositSize = builder.comment("The minimum amount of connected blocks to be considered a deposit").define("prospector_min_deposit_size", 32);
        sampleProximityRange = builder.comment("The distance between ore samples of the same type that duplicate waypoint creation should be blocked in").define("prospector_proximity", 16);
        builder.build();
    }
}