package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.config.CataclysmExpeditionsConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
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
      List.of(new BlockPos(27, 37, -35)),
      Component.literal("Netherite\nMonstrosity").withStyle(getDisplayStyle()),
      null,
      data -> {}
  ),
  ENDER_GUARDIAN(
      "ender_guardian",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ruined_citadel"),
      false,
      new DetailedPosition(-22.5f, 56f, 11.5f, 270),
      new DetailedPosition(-1597.5f, 2f, -1582.5f, -90),
      new BlockPosPair(new BlockPos(-88, -64, -90), new BlockPos(126, 300, 123)),
      List.of(new BlockPos(-4, 54, 14)),
      Component.literal("Ender\nGuardian").withStyle(getDisplayStyle()),
      null,
      data -> {}
  ),
  THE_HARBINGER(
      "the_harbinger",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "ancient_factory"),
      false,
      new DetailedPosition(58.5f, -25f, 2.5f, 0),
      new DetailedPosition(-1581.5f, 2f, -1574.5f, 90),
      new BlockPosPair(new BlockPos(-84, -64, -55), new BlockPos(120, 300, 116)),
      List.of(new BlockPos(58, -25, 13)),
      Component.literal("The Harbinger").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.the_harbinger").withStyle(getNoteStyle()),
      data -> {}
  ),
  ANCIENT_REMNANT(
      "ancient_remnant",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "cursed_pyramid"),
      false,
      new DetailedPosition(51.5f, 7f, 111.5f, 180),
      new DetailedPosition(-1597.5f, 2f, -1574.5f, -90),
      new BlockPosPair(new BlockPos(-45, -64, -46), new BlockPos(153, 300, 167)),
      List.of(new BlockPos(53, 7, 105)),
      Component.literal("Ancient\nRemnant").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.ancient_remnant").withStyle(getNoteStyle()),
      data -> {}
  ),
  THE_LEVIATHAN(
      "the_leviathan",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "sunken_city"),
      false,
      new DetailedPosition(-100.5f, 26f, 23.5f, 270),
      new DetailedPosition(-1581.5f, 2f, -1566.5f, 90),
      new BlockPosPair(new BlockPos(-170, -64, -130), new BlockPos(180, 300, 152)),
      List.of(new BlockPos(-87, 26, 26)),
      Component.literal("The Leviathan").withStyle(getDisplayStyle()),
      Component.translatable("display.cataclysm_expeditions.note.the_leviathan").withStyle(getNoteStyle()),
      data -> {}
  ),
  SCYLLA(
      "scylla",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "acropolis"),
      false,
      new DetailedPosition(0.5f, 66f, 42.5f, 0),
      new DetailedPosition(-1597.5f, 2f, -1566.5f, -90),
      new BlockPosPair(new BlockPos(-152, -64, -88), new BlockPos(170, 300, 150)),
      List.of(new BlockPos(5, 92, 47), new BlockPos(99, 185, 24), new BlockPos(0, 242, 74)),
      Component.literal("Scylla").withStyle(getDisplayStyle()),
      null,
      data -> {}
  ),
  MALEDICTUS(
      "maledictus",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "frosted_prison"),
      false,
      new DetailedPosition(10.5f, 13f, 80.5f, 180),
      new DetailedPosition(-1581.5f, 2f, -1558.5f, 90),
      new BlockPosPair(new BlockPos(-103, -64, -105), new BlockPos(184, 300, 159)),
      List.of(new BlockPos(13, 17, 72), new BlockPos(27, 17, -12)),
      Component.literal("Maledictus").withStyle(getDisplayStyle()),
      null,
      data -> {}
  ),
  IGNIS(
      "ignis",
      ResourceLocation.fromNamespaceAndPath("cataclysm", "burning_arena"),
      true,
      new DetailedPosition(42.5f, 32f, 8.5f, 0),
      new DetailedPosition(-1597.5f, 2f, -1558.5f, -90),
      new BlockPosPair(new BlockPos(-53, -64, -54), new BlockPos(140, 300, 140)),
      List.of(new BlockPos(42, 32, 25)),
      Component.literal("Ignis").withStyle(getDisplayStyle()),
      null,
      data -> {
        // removing the netherrack ceiling that gets shipped with the structure
        for (int xOffset = 11; xOffset <= 73; xOffset++) {
          for (int zOffset = 12; zOffset <= 70; zOffset++) {
            data.level.setBlock(data.startPos.offset(xOffset, 101, zOffset), Blocks.AIR.defaultBlockState(), 2 | 16);
          }
        }
      }
  );

  private static Style getDisplayStyle() {
    return Style.EMPTY.withColor(TextColor.fromRgb(0xd19ffc));
  }

  private static Style getNoteStyle() {
    return Style.EMPTY.withColor(ChatFormatting.RED);
  }

  public DetailedPosition getTeleportationOffset() {
    return DetailedPosition.fromString(CataclysmExpeditionsConfig.CONFIG.expeditionPositions.get(this).teleportOffset().get());
  }

  public List<BlockPos> getAnchorOffsets() {
    return CataclysmExpeditionsConfig.CONFIG.expeditionPositions.get(this).anchorOffsets().get()
        .stream().map(BlockPosUtils::fromString).toList();
  }

  public BlockPosPair getStructureBoundaryOffsets() {
    return BlockPosPair.fromString(CataclysmExpeditionsConfig.CONFIG.expeditionPositions.get(this).structureBoundaryOffsets().get());
  }

  @Getter
  private final String id;

  @Getter
  private final ResourceLocation structureLocation;

  @Getter
  private final boolean isNether;

  @Getter
  private final DetailedPosition defaultTeleportationOffset;

  @Getter
  private final DetailedPosition uiPosition;

  @Getter
  private final BlockPosPair defaultStructureBoundaryOffsets;

  @Getter
  private final List<BlockPos> defaultAnchorOffsets;

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
