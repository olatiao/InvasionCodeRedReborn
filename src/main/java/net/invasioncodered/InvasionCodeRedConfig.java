package net.invasioncodered;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class InvasionCodeRedConfig {
    public static final ForgeConfigSpec SERVER_SPEC;

    public static ForgeConfigSpec.BooleanValue gashslitInRaid;
    public static ForgeConfigSpec.ConfigValue<Integer> gashslitSpawnChance;

    private static ForgeConfigSpec.Builder builder;

    static {
        builder = new ForgeConfigSpec.Builder();

        builder.push("Settings");
        gashslitInRaid = builder.comment("enable gashslit spawning in raid").worldRestart().define("gashslitInRaid",
                true);
        gashslitSpawnChance = builder.comment("chance for gashslit appearing in raid").worldRestart()
                .defineInRange("gashslitSpawnChance", 4, 0, Integer.MAX_VALUE);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    public static boolean gashslitInRaid() {
        return gashslitInRaid.get();
    }

    public static int gashslitSpawnChance() {
        return gashslitSpawnChance.get();
    }

    public static void register() {
        @SuppressWarnings("removal")
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    }
}
