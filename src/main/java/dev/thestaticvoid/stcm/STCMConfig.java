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

    private STCMConfig(ModConfigSpec.Builder builder){
        lootrMinXp = builder.comment("The minimum amount of XP gained from opening Lootr chests.").define("lootr_min_xp", 15);
        lootrMaxXp = builder.comment("The maximum amount of XP gained from opening Lootr chests.").define("lootr_max_xp", 20);
        builder.build();
    }
}