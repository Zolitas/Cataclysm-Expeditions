package de.zolitas.cataclysmexpeditions.world;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ExpeditionWorldUtils {
  private static final ResourceLocation EXPEDITION_DIMENSION_LOCATION = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_world");
  private static final ResourceLocation EXPEDITION_DIMENSION_LOCATION_NETHER = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_world_nether");
  public static final ResourceLocation HUB_STRUCTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "hub");

  public static boolean isExpeditionDimension(ResourceLocation dimensionLocation) {
    return dimensionLocation.equals(EXPEDITION_DIMENSION_LOCATION) || dimensionLocation.equals(EXPEDITION_DIMENSION_LOCATION_NETHER);
  }

  public static @Nullable ServerLevel getExpeditionLevel(MinecraftServer server, boolean isNether) {
    ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, isNether ? EXPEDITION_DIMENSION_LOCATION_NETHER : EXPEDITION_DIMENSION_LOCATION);
    return server.getLevel(levelKey);
  }

  public static ExpeditionWorldSavedData getExpeditionWorldSavedData(MinecraftServer server, boolean isNether) {
    ServerLevel expeditionLevel = getExpeditionLevel(server, isNether);
    assert expeditionLevel != null;
    return getExpeditionWorldSavedData(expeditionLevel);
  }

  public static ExpeditionWorldSavedData getExpeditionWorldSavedData(ServerLevel expeditionLevel) {
    return expeditionLevel.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ExpeditionWorldSavedData::new, ExpeditionWorldSavedData::load), "expedition_data");
  }

  public static void teleportToHub(ServerPlayer player) {
    MinecraftServer server = player.getServer();
    if (server == null) {
      throw new IllegalStateException("Server of player is null!");
    }

    try {
      generateHubStructure(server);
    } catch (Exception exception) {
      throw new RuntimeException("Failed to generate hub structure!", exception);
    }

    player.teleportTo(Objects.requireNonNull(getExpeditionLevel(server, false)), -1609.5, 1, -1604.5, 180, 0);
  }

  private static void generateHubStructure(MinecraftServer server) {
    ServerLevel level = getExpeditionLevel(server, false);
    assert level != null;

    ExpeditionWorldSavedData expeditionWorldSavedData = getExpeditionWorldSavedData(level);

    if (expeditionWorldSavedData.isHubGenerated()) {
      return;
    }

    ChunkGenerator chunkGenerator = level.getChunkSource().getGenerator();

    RegistryAccess.Frozen registryAccess = server.registryAccess();

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure hubStructure = structureRegistry.get(HUB_STRUCTURE_LOCATION);

    assert hubStructure != null;
    StructureStart structureStart = hubStructure.generate(
        registryAccess,
        chunkGenerator,
        chunkGenerator.getBiomeSource(),
        level.getChunkSource().randomState(),
        level.getStructureManager(),
        level.getSeed(),
        new ChunkPos(-100, -100),
        0,
        level,
        biomeHolder -> true
    );

    assert structureStart.isValid();

    placeStructure(level, chunkGenerator, structureStart);
    expeditionWorldSavedData.setHubGenerated(true);
  }

  public static void placeStructure(ServerLevel level, ChunkGenerator chunkGenerator, StructureStart structureStart) {
    BoundingBox boundingBox = structureStart.getBoundingBox();
    ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
    ChunkPos chunkPos2 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));

    ChunkPos
        .rangeClosed(chunkPos1, chunkPos2)
        .forEach(chunkPos -> {
          structureStart.placeInChunk(
              level,
              level.structureManager(),
              chunkGenerator,
              level.getRandom(),
              new BoundingBox(
                  chunkPos.getMinBlockX(),
                  level.getMinBuildHeight(),
                  chunkPos.getMinBlockZ(),
                  chunkPos.getMaxBlockX(),
                  level.getMaxBuildHeight(),
                  chunkPos.getMaxBlockZ()
              ),
              chunkPos
          );
        });
  }
}
