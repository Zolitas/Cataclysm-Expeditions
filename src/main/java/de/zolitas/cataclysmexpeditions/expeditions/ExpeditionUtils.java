package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.config.CataclysmExpeditionsConfig;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExpeditionUtils {
  public static void startExpedition(Expedition expedition, Collection<ServerPlayer> targets,
                                     MinecraftServer server, RegistryAccess registryAccess, Consumer<ExpeditionException> exceptionHandler)
  {
    ServerLevel expeditionLevel = ExpeditionWorldUtils.getExpeditionLevel(server, expedition.isNether());

    if (expeditionLevel == null) {
      exceptionHandler.accept(new ExpeditionException(Component.translatable("error.cataclysm_expeditions.start.no_dimension")));
      return;
    }

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure structure = structureRegistry.get(expedition.getStructureLocation());

    if (structure == null) {
      exceptionHandler.accept(new ExpeditionException(Component.translatable("error.cataclysm_expeditions.start.no_structure")));
      return;
    }

    ExpeditionLobby lobby = ExpeditionLobbyUtils.createLobby(expedition, server);
    targets.forEach(lobby::addPlayer);

    int expeditionCounter = ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).getExpeditionCounter();

    int distance = CataclysmExpeditionsConfig.CONFIG.distanceBetweenExpeditionStructures.get();
    ChunkPos placementChunkPos = new ChunkPos(
        (expeditionCounter % distance) * distance,
        (expeditionCounter / distance) * distance
    );

    BiConsumer<Integer, Integer> progressHandler = (placed, total) -> {
      try {
        lobby.setGenerationProgress((float) placed / total);
      }
      catch (Exception exception) {
        lobby.setGenerationProgress(0);
      }
    };

    placeExpeditionStructure(registryAccess, expeditionLevel, structure, placementChunkPos, progressHandler)
        .thenRun(() -> {
          ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).incrementExpeditionCounter();

          ExpeditionCallbackData expeditionCallbackData = ExpeditionCallbackData.builder()
              .players(targets)
              .level(expeditionLevel)
              .startPos(new BlockPos(placementChunkPos.getMinBlockX(), 0, placementChunkPos.getMinBlockZ()))
              .build();

          expedition.getCallback().accept(expeditionCallbackData);

          lobby.finishedGenerating();
          lobby.setCallbackData(expeditionCallbackData);
          lobby.teleportAllPlayers();
        })
        .exceptionally(exception -> {
          ExpeditionLobbyUtils.deleteLobby(expedition);
          exceptionHandler.accept(new ExpeditionException(Component.literal(exception.getMessage())));
          return null;
        });
  }

  private static CompletableFuture<Void> placeExpeditionStructure(RegistryAccess registryAccess, ServerLevel expeditionLevel,
                                                                  Structure structure, ChunkPos placementChunkPos,
                                                                  BiConsumer<Integer, Integer> progressHandler)
  {
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
      return CompletableFuture.failedFuture(new ExpeditionException(Component.translatable("error.cataclysm_expeditions.structure_generation_failed")));
    }

    return ExpeditionWorldUtils.placeStructure(expeditionLevel, chunkGenerator, structureStart, progressHandler);
  }
}
