package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
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
      new DetailedPosition(27.5f, 37f, -38.5f, 0),
      new DetailedPosition(-1581.5f, 2f, -1582.5f, 90),
      new BlockPosPair(new BlockPos(-48, -64, -92), new BlockPos(108, 300, 91)),
      Component.literal("Netherite\nMonstrosity").withStyle(getDisplayStyle()),
      null,
      data -> {
        placeExpeditionAnchor(data, 27, 37, -35);
      }
  ),
  ENDER_GUARDIAN(
      "ender_guardian",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ruined_citadel"),
      false,
      new DetailedPosition(-22.5f, 56f, 11.5f, 270),
      new DetailedPosition(-1597.5f, 2f, -1582.5f, -90),
      new BlockPosPair(new BlockPos(-88, -64, -90), new BlockPos(126, 300, 123)),
      Component.literal("Ender\nGuardian").withStyle(getDisplayStyle()),
      null,
      data -> {
        placeExpeditionAnchor(data, -4, 54, 14);
      }
  ),
  THE_HARBINGER(
      "the_harbinger",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ancient_factory"),
      false,
      new DetailedPosition(58.5f, -25f, 2.5f, 0),
      new DetailedPosition(-1581.5f, 2f, -1574.5f, 90),
      new BlockPosPair(new BlockPos(-84, -64, -55), new BlockPos(120, 300, 116)),
      Component.literal("The Harbinger").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.the_harbinger").withStyle(getNoteStyle()),
      data -> {
        placeExpeditionAnchor(data, 58, -25, 13);
      }
  ),
  ANCIENT_REMNANT(
      "ancient_remnant",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "cursed_pyramid"),
      false,
      new DetailedPosition(51.5f, 7f, 111.5f, 180),
      new DetailedPosition(-1597.5f, 2f, -1574.5f, -90),
      new BlockPosPair(new BlockPos(-45, -64, -46), new BlockPos(153, 300, 167)),
      Component.literal("Ancient\nRemnant").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.ancient_remnant").withStyle(getNoteStyle()),
      data -> {
        placeExpeditionAnchor(data, 53, 7, 105);
      }
  ),
  THE_LEVIATHAN(
      "the_leviathan",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "sunken_city"),
      false,
      new DetailedPosition(-100.5f, 26f, 23.5f, 270),
      new DetailedPosition(-1581.5f, 2f, -1566.5f, 90),
      new BlockPosPair(new BlockPos(-170, -64, -130), new BlockPos(180, 300, 152)),
      Component.literal("The Leviathan").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.the_leviathan").withStyle(getNoteStyle()),
      data -> {
        placeExpeditionAnchor(data, -87, 26, 26);
      }
  ),
  SCYLLA(
      "scylla",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "acropolis"),
      false,
      new DetailedPosition(0.5f, 66f, 42.5f, 0),
      new DetailedPosition(-1597.5f, 2f, -1566.5f, -90),
      new BlockPosPair(new BlockPos(-152, -64, -88), new BlockPos(170, 300, 150)),
      Component.literal("Scylla").withStyle(getDisplayStyle()),
      null,
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
      new DetailedPosition(10.5f, 13f, 80.5f, 180),
      new DetailedPosition(-1581.5f, 2f, -1558.5f, 90),
      new BlockPosPair(new BlockPos(-103, -64, -105), new BlockPos(184, 300, 159)),
      Component.literal("Maledictus").withStyle(getDisplayStyle()),
      null,
      data -> {
        placeExpeditionAnchor(data, 13, 17, 72);
        placeExpeditionAnchor(data, 27, 17, -12);
      }
  ),
  IGNIS(
      "ignis",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "burning_arena"),
      true,
      new DetailedPosition(42.5f, 32f, 8.5f, 0),
      new DetailedPosition(-1597.5f, 2f, -1558.5f, -90),
      new BlockPosPair(new BlockPos(-53, -64, -54), new BlockPos(140, 300, 140)),
      Component.literal("Ignis").withStyle(getDisplayStyle()),
      null,
      data -> {
        placeExpeditionAnchor(data, 42, 32, 25);
      }
  );

  private static Style getDisplayStyle() {
    return Style.EMPTY.withColor(TextColor.fromRgb(0xd19ffc));
  }

  private static Style getNoteStyle() {
    return Style.EMPTY.withColor(ChatFormatting.RED);
  }

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
  private final DetailedPosition teleportationOffset;

  @Getter
  private final DetailedPosition uiPosition;

  @Getter
  private final BlockPosPair structureBoundaryOffsets;

  @Getter
  private final Component displayComponent;

  @Getter
  @Nullable
  private final Component noteComponent;

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
