package de.zolitas.cataclysmexpeditions.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CataclysmExpeditionsConfig {
  public static final CataclysmExpeditionsConfig CONFIG;
  public static final ModConfigSpec CONFIG_SPEC;

  public final ModConfigSpec.ConfigValue<Integer> maxExpeditionPlayerCount;
  public final ModConfigSpec.ConfigValue<Integer> expeditionLobbyTtl;
  public final ModConfigSpec.ConfigValue<Integer> expeditionCooldown;
  public final ModConfigSpec.ConfigValue<Integer> distanceBetweenExpeditionStructures;
  public final ModConfigSpec.ConfigValue<Boolean> hubAnchorRecipeEnabled;

  private CataclysmExpeditionsConfig(ModConfigSpec.Builder builder) {
    maxExpeditionPlayerCount = builder
        .comment("Maximum number of players allowed in an expedition. The UI might not look good with more than 4 players")
        .defineInRange("maxExpeditionPlayerCount", 4, 1, 100);

    expeditionLobbyTtl = builder
        .comment("Number of ticks that an expedition should be open for others to join. Is displayed as seconds ingame")
        .defineInRange("expeditionLobbyTtl", 20 * 30, 1, Integer.MAX_VALUE);

    expeditionCooldown = builder
        .comment("Number of ticks that a player has to access the same one again. Is displayed as hours, minutes and seconds ingame")
        .defineInRange("expeditionCooldown", 20 * 60 * 60 * 24, 1, Integer.MAX_VALUE);

    distanceBetweenExpeditionStructures = builder
        .comment("Distance between the expedition structures in chunks")
        .defineInRange("distanceBetweenExpeditionStructures", 100, 10, Integer.MAX_VALUE);

    hubAnchorRecipeEnabled = builder
        .comment("Enables the recipe for the hub anchor that is needed to enter the hub. If you disable this setting, you will have to implement another way to get to the hub yourself")
        .define("hubAnchorRecipeEnabled", true);
  }

  static {
    Pair<CataclysmExpeditionsConfig, ModConfigSpec> modConfigPair = new ModConfigSpec.Builder().configure(CataclysmExpeditionsConfig::new);
    CONFIG = modConfigPair.getLeft();
    CONFIG_SPEC = modConfigPair.getRight();
  }
}
