package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

@AllArgsConstructor
public enum Expeditions {
  NETHERITE_MONSTROSITY(
      "netherite_monstrosity",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "soul_black_smith"),
      true,
      data -> teleportPlayersToStart(data, 27.5f, 37f, -38.5f, 0)
  ),
  ENDER_GUARDIAN(
      "ender_guardian",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ruined_citadel"),
      false,
      data -> teleportPlayersToStart(data, -22.5f, 56f, 11.5f, 270)
  ),
  THE_HARBINGER( //todo: add note that a nether star is required
      "the_harbinger",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ancient_factory"),
      false,
      data -> teleportPlayersToStart(data, 58.5f, -25f, 2.5f, 0)
  ),
  ANCIENT_REMNANT( //todo: add note that a brush is required
      "ancient_remnant",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "cursed_pyramid"),
      false,
      data -> teleportPlayersToStart(data, 51.5f, 7f, 111.5f, 180)
  ),
  THE_LEVIATHAN( //todo: add note that a heart of the sea is required / fix buried treasure not being present
      "the_leviathan",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "sunken_city"),
      false,
      data -> teleportPlayersToStart(data, -100.5f, 26f, 23.5f, 270)
  ),
  SCYLLA(
      "scylla",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "acropolis"),
      false,
      data -> teleportPlayersToStart(data, 0.5f, 66f, 42.5f, 0)
  ),
  MALEDICTUS(
      "maledictus",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "frosted_prison"),
      false,
      data -> teleportPlayersToStart(data, 10.5f, -57, 80.5f, 180)
  ),
  IGNIS(
      "ignis",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "burning_arena"),
      true,
      data -> teleportPlayersToStart(data, 42.5f, 32f, 8.5f, 0)
  );

  private static void teleportPlayersToStart(ExpeditionCallbackData data, float offsetX, float offsetY, float offsetZ, int yaw) {
    BlockPos startPos = data.startPos;
    for (ServerPlayer player : data.players) {
      player.teleportTo(data.level, startPos.getX() + offsetX, startPos.getY() + offsetY, startPos.getZ() + offsetZ, yaw, 0);
    }
  }

  @Getter
  private final String id;

  @Getter
  private final ResourceLocation structureLocation;

  @Getter
  private final boolean isNether;

  @Getter
  private final Consumer<ExpeditionCallbackData> callback;

  public static @Nullable Expeditions getById(String id) {
    return Arrays
        .stream(Expeditions.values())
        .filter(expedition -> expedition.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
