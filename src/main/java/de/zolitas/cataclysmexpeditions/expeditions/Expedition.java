package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

@AllArgsConstructor
public enum Expedition implements StringRepresentable {
  NETHERITE_MONSTROSITY(
      "netherite_monstrosity",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "soul_black_smith"),
      true,
      new TeleportationOffset(27.5f, 37f, -38.5f, 0),
      data -> {
        placeExpeditionAnchor(data, 27, 37, -35);
      }
  ),
  ENDER_GUARDIAN(
      "ender_guardian",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ruined_citadel"),
      false,
      new TeleportationOffset(-22.5f, 56f, 11.5f, 270),
      data -> {
        placeExpeditionAnchor(data, -4, 54, 14);
      }
  ),
  THE_HARBINGER( //todo: add note that a nether star is required
      "the_harbinger",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ancient_factory"),
      false,
      new TeleportationOffset(58.5f, -25f, 2.5f, 0),
      data -> {
        placeExpeditionAnchor(data, 58, -25, 13);
      }
  ),
  ANCIENT_REMNANT( //todo: add note that a brush is required
      "ancient_remnant",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "cursed_pyramid"),
      false,
      new TeleportationOffset(51.5f, 7f, 111.5f, 180),
      data -> {
        placeExpeditionAnchor(data, 53, 7, 105);
      }
  ),
  THE_LEVIATHAN( //todo: add note that a heart of the sea is required / fix buried treasure not being present
      "the_leviathan",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "sunken_city"),
      false,
      new TeleportationOffset(-100.5f, 26f, 23.5f, 270),
      data -> {
        placeExpeditionAnchor(data, -87, 26, 26);
      }
  ),
  SCYLLA(
      "scylla",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "acropolis"),
      false,
      new TeleportationOffset(0.5f, 66f, 42.5f, 0),
      data -> {
        placeExpeditionAnchor(data, 5, 92, 47);
        placeExpeditionAnchor(data, 99, 185, 24);
        placeExpeditionAnchor(data, 0, 242, 74);
      }
  ),
  MALEDICTUS(
      "maledictus",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "frosted_prison"),
      false,
      new TeleportationOffset(10.5f, 13f, 80.5f, 180),
      data -> {
        placeExpeditionAnchor(data, 13, 17, 72);
        placeExpeditionAnchor(data, 27, 17, -12);
      }
  ),
  IGNIS(
      "ignis",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "burning_arena"),
      true,
      new TeleportationOffset(42.5f, 32f, 8.5f, 0),
      data -> {
        placeExpeditionAnchor(data, 42, 32, 25);
      }
  );

  private static void placeExpeditionAnchor(ExpeditionCallbackData data, int offsetX, int offsetY, int offsetZ) {
    BlockPos anchorPos = data.startPos.offset(offsetX, offsetY, offsetZ);
    data.level.setBlock(anchorPos, BlocksRegister.EXPEDITION_ANCHOR_BLOCK.get().defaultBlockState(), 3);
  }

  @Getter
  private final String id;

  @Getter
  private final ResourceLocation structureLocation;

  @Getter
  private final boolean isNether;

  @Getter
  private final TeleportationOffset teleportationOffset;

  @Getter
  private final Consumer<ExpeditionCallbackData> callback;

  public static @Nullable Expedition getById(String id) {
    return Arrays
        .stream(Expedition.values())
        .filter(expedition -> expedition.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public @NotNull String getSerializedName() {
    return id;
  }
}
