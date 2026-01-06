package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;

public class ExpeditionUtils {
  public static void startExpedition(Expeditions expedition, Collection<ServerPlayer> targets, MinecraftServer server, RegistryAccess registryAccess)
      throws ExpeditionException {
    ServerLevel expeditionLevel = ExpeditionWorldUtils.getExpeditionLevel(server, expedition.isNether());

    if (expeditionLevel == null) {
      throw new ExpeditionException("Expedition dimension not found!");
    }

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure structure = structureRegistry.get(expedition.getStructureLocation());

    if (structure == null) {
      throw new ExpeditionException("Expedition structure not found!");
    }

    int expeditionCounter = ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).getExpeditionCounter();

    ChunkPos placementChunkPos = new ChunkPos((expeditionCounter % 100) * 100, (expeditionCounter / 100) * 100);

    placeExpeditionStructure(registryAccess, expeditionLevel, structure, placementChunkPos);

    ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).incrementExpeditionCounter();

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

    ExpeditionWorldUtils.placeStructure(expeditionLevel, chunkGenerator, structureStart);
  }
}
