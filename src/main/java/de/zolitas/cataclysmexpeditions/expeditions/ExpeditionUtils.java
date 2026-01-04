package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

import java.util.Collection;

public class ExpeditionUtils {
  public static @Nullable ServerLevel getExpeditionLevel(MinecraftServer server, boolean isNether) {
    ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, isNether ? CataclysmExpeditions.EXPEDITION_DIMENSION_LOCATION_NETHER : CataclysmExpeditions.EXPEDITION_DIMENSION_LOCATION);
    return server.getLevel(levelKey);
  }

  public static ExpeditionWorldSavedData getExpeditionWorldSavedData(MinecraftServer server, boolean isNether) {
    ServerLevel expeditionLevel = getExpeditionLevel(server, isNether);
    assert expeditionLevel != null;
    return expeditionLevel.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ExpeditionWorldSavedData::new, ExpeditionWorldSavedData::load), "expedition_data");
  }

  public static void startExpedition(Expeditions expedition, Collection<ServerPlayer> targets, MinecraftServer server, RegistryAccess registryAccess)
      throws ExpeditionException {
    ServerLevel expeditionLevel = getExpeditionLevel(server, expedition.isNether());

    if (expeditionLevel == null) {
      throw new ExpeditionException("Expedition dimension not found!");
    }

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure structure = structureRegistry.get(expedition.getStructureLocation());

    if (structure == null) {
      throw new ExpeditionException("Expedition structure not found!");
    }

    int expeditionCounter = getExpeditionWorldSavedData(server, expedition.isNether()).getExpeditionCounter();

    ChunkPos placementChunkPos = new ChunkPos((expeditionCounter % 100) * 100, (expeditionCounter / 100) * 100);

    placeExpeditionStructure(registryAccess, expeditionLevel, structure, placementChunkPos);

    getExpeditionWorldSavedData(server, expedition.isNether()).incrementExpeditionCounter();

    ExpeditionCallbackData expeditionCallbackData = ExpeditionCallbackData.builder()
        .players(targets)
        .level(expeditionLevel)
        .startPos(new BlockPos(placementChunkPos.getMinBlockX(), 0, placementChunkPos.getMinBlockZ()))
        .build();

    expedition.getCallback().accept(expeditionCallbackData);
  }

  private static void placeExpeditionStructure(RegistryAccess registryAccess, ServerLevel expeditionLevel, Structure structure, ChunkPos placementChunkPos) throws ExpeditionException {
    ChunkGenerator chunkGenerator = expeditionLevel.getChunkSource().getGenerator();
    StructureStart structureStart = structure.generate(
        registryAccess,
        chunkGenerator,
        chunkGenerator.getBiomeSource(),
        expeditionLevel.getChunkSource().randomState(),
        expeditionLevel.getStructureManager(),
        expeditionLevel.getSeed(),
        placementChunkPos,
        0,
        expeditionLevel,
        biomeHolder -> true
    );

    if (!structureStart.isValid()) {
      throw new ExpeditionException("Expedition structure could not be generated!");
    }

    BoundingBox boundingBox = structureStart.getBoundingBox();
    ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
    ChunkPos chunkPos2 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));

    ChunkPos
        .rangeClosed(chunkPos1, chunkPos2)
        .forEach(chunkPos -> {
          structureStart.placeInChunk(
              expeditionLevel,
              expeditionLevel.structureManager(),
              chunkGenerator,
              expeditionLevel.getRandom(),
              new BoundingBox(
                  chunkPos.getMinBlockX(),
                  expeditionLevel.getMinBuildHeight(),
                  chunkPos.getMinBlockZ(),
                  chunkPos.getMaxBlockX(),
                  expeditionLevel.getMaxBuildHeight(),
                  chunkPos.getMaxBlockZ()
              ),
              chunkPos
          );
        });
  }
}
