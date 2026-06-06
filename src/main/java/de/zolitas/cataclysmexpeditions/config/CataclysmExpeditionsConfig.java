package de.zolitas.cataclysmexpeditions.config;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.expeditions.BlockPosPair;
import de.zolitas.cataclysmexpeditions.expeditions.BlockPosUtils;
import de.zolitas.cataclysmexpeditions.expeditions.DetailedPosition;
import de.zolitas.cataclysmexpeditions.expeditions.Expedition;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.List;

public class CataclysmExpeditionsConfig {
  public static final CataclysmExpeditionsConfig CONFIG;
  public static final ModConfigSpec CONFIG_SPEC;

  public final ModConfigSpec.ConfigValue<Integer> maxExpeditionPlayerCount;
  public final ModConfigSpec.ConfigValue<Integer> expeditionLobbyTtl;
  public final ModConfigSpec.ConfigValue<Integer> expeditionCooldown;
  public final ModConfigSpec.ConfigValue<Integer> distanceBetweenExpeditionStructures;
  public final ModConfigSpec.ConfigValue<Boolean> hubAnchorRecipeEnabled;
  public final ModConfigSpec.ConfigValue<Integer> structureGenerationBatchSize;
  public final EnumMap<Expedition, ExpeditionPositions> expeditionPositions = new EnumMap<>(Expedition.class);

  public record ExpeditionPositions(
      ModConfigSpec.ConfigValue<String> teleportOffset,
      ModConfigSpec.ConfigValue<List<? extends String>> anchorOffsets,
      ModConfigSpec.ConfigValue<String> structureBoundaryOffsets
  ) {}

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

    structureGenerationBatchSize = builder
        .comment("Number of chunks of a structure that are generated in one batch. Higher values lead to longer loading times but also lower lag spikes")
        .defineInRange("structureGenerationBatchSize", 2, 1, 5);

    defineExpeditionPositionConfig(builder);
  }

  private void defineExpeditionPositionConfig(ModConfigSpec.Builder builder) {
    for (Expedition expedition : Expedition.values()) {
      ModConfigSpec.ConfigValue<String> teleportOffset = builder
          .comment(String.format("""
              Defines the position of where the player will be teleported when starting a "%s" expedition
              The position must be four numbers separated by spaces. Three floats for the coordinates and one integer for the yaw.
              """, expedition.getId()))
          .define(String.format("expeditionPositions.%s.teleportOffset", expedition.getId()), expedition.getDefaultTeleportationOffset().toString(), object -> {
            if (object instanceof String string) {
              try {
                DetailedPosition.fromString(string);
                return true;
              }
              catch (Exception exception)
              {
                CataclysmExpeditions.LOGGER.error("Failed to parse teleport offset for expedition '{}': {}", expedition.getId(), exception.getMessage());
                return false;
              }
            }
            return false;
          });

      ModConfigSpec.ConfigValue<List<? extends String>> anchorOffsets = builder
          .comment(String.format("""
              Defines the positions of the anchors for the "%s" expedition
              Each position is three integers (x y z) separated by spaces.
              """, expedition.getId()))
          .defineList(
              String.format("expeditionPositions.%s.anchorOffsets", expedition.getId()),
              () -> expedition.getDefaultAnchorOffsets().stream().map(BlockPosUtils::toString).toList(),
              () -> "0 0 0",
              object -> {
                if (!(object instanceof String string)) return false;
                try {
                  BlockPosUtils.fromString(string);
                } catch (Exception exception) {
                  CataclysmExpeditions.LOGGER.error("Failed to parse anchor offset for expedition '{}': {}", expedition.getId(), exception.getMessage());
                  return false;

                }
                return true;
              }
          );

      ModConfigSpec.ConfigValue<String> structureBoundaryOffsets = builder
          .comment(String.format("""
              Defines the bounding box of the "%s" expedition via two opposite corners
              Each corner is three integers (x y z) separated by spaces; the two corners are separated by a colon.
              """, expedition.getId()))
          .define(String.format("expeditionPositions.%s.structureBoundaryOffsets", expedition.getId()), expedition.getDefaultStructureBoundaryOffsets().toString(), object -> {
            if (object instanceof String string) {
              try {
                BlockPosPair.fromString(string);
                return true;
              }
              catch (Exception exception)
              {
                CataclysmExpeditions.LOGGER.error("Failed to parse structure boundary offsets for expedition '{}': {}", expedition.getId(), exception.getMessage());
                return false;
              }
            }
            return false;
          });


      ExpeditionPositions currentPositions = new ExpeditionPositions(teleportOffset, anchorOffsets, structureBoundaryOffsets);
      expeditionPositions.put(expedition, currentPositions);
    }
  }

  static {
    Pair<CataclysmExpeditionsConfig, ModConfigSpec> modConfigPair = new ModConfigSpec.Builder().configure(CataclysmExpeditionsConfig::new);
    CONFIG = modConfigPair.getLeft();
    CONFIG_SPEC = modConfigPair.getRight();
  }
}
