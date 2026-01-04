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
      data -> {
        BlockPos startPos = data.startPos;
        for (ServerPlayer player : data.players) {
          player.teleportTo(data.level, startPos.getX() + 27.5, startPos.getY() + 37, startPos.getZ() - 38.5, 0, 0);
        }
      }
  );

  @Getter
  private final String id;

  @Getter
  private final ResourceLocation structureLocation;

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
