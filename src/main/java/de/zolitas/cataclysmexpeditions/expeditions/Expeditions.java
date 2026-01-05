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
      data -> {
        BlockPos startPos = data.startPos;
        for (ServerPlayer player : data.players) {
          player.teleportTo(data.level, startPos.getX() + 27.5, startPos.getY() + 37, startPos.getZ() - 38.5, 0, 0);
        }
      }
  ),
  ENDER_GUARDIAN(
      "ender_guardian",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ruined_citadel"),
      false,
      data -> {
        BlockPos startPos = data.startPos;
        for (ServerPlayer player : data.players) {
          player.teleportTo(data.level, startPos.getX() - 22.5, startPos.getY() + 56, startPos.getZ() + 11.5, 270, 0);
        }
      }
  ),
  THE_HARBINGER( //todo: add note that a nether star is required
      "the_harbinger",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ancient_factory"),
      false,
      data -> {
        BlockPos startPos = data.startPos;
        for (ServerPlayer player : data.players) {
          player.teleportTo(data.level, startPos.getX() + 58.5, startPos.getY() - 25, startPos.getZ() + 2.5, 0, 0);
        }
      }
  );

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
