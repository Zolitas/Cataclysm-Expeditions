package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.blocks.BlocksRegister;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
    ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).incrementExpeditionCounter();

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
          BlockPos startPos = new BlockPos(placementChunkPos.getMinBlockX(), 0, placementChunkPos.getMinBlockZ());
          
          placeExpeditionStructureBoundary(expeditionLevel, expedition.getStructureBoundaryOffsets(), startPos);

          ExpeditionCallbackData expeditionCallbackData = ExpeditionCallbackData.builder()
              .players(targets)
              .level(expeditionLevel)
              .startPos(startPos)
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

  private static void placeExpeditionStructureBoundary(ServerLevel level, BlockPosPair boundaryOffsets, BlockPos startPos) {
    int minX = startPos.getX() + Math.min(boundaryOffsets.getFirstPos().getX(), boundaryOffsets.getSecondPos().getX());
    int maxX = startPos.getX() + Math.max(boundaryOffsets.getFirstPos().getX(), boundaryOffsets.getSecondPos().getX());
    int minY = startPos.getY() + Math.min(boundaryOffsets.getFirstPos().getY(), boundaryOffsets.getSecondPos().getY());
    int maxY = startPos.getY() + Math.max(boundaryOffsets.getFirstPos().getY(), boundaryOffsets.getSecondPos().getY());
    int minZ = startPos.getZ() + Math.min(boundaryOffsets.getFirstPos().getZ(), boundaryOffsets.getSecondPos().getZ());
    int maxZ = startPos.getZ() + Math.max(boundaryOffsets.getFirstPos().getZ(), boundaryOffsets.getSecondPos().getZ());

    BlockState barrier = Blocks.BARRIER.defaultBlockState();
    BlockState voidDeath = BlocksRegister.VOID_DEATH_BLOCK.get().defaultBlockState();
    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    int flags = 2 | 16;

    // bottom
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        level.setBlock(pos.set(x, minY + 2, z), voidDeath, flags);
        level.setBlock(pos.set(x, minY + 1, z), voidDeath, flags);
        level.setBlock(pos.set(x, minY, z), voidDeath, flags);
      }
    }

    // top
    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        level.setBlock(pos.set(x, maxY, z), barrier, flags);
        level.setBlock(pos.set(x, maxY - 1, z), barrier, flags);
      }
    }

    int wallMinY = minY + 2;
    int wallMaxY = maxY - 2;

    for (int layer = 0; layer < 2; layer++) {
      for (int z = minZ; z <= maxZ; z++) {
        for (int y = wallMinY; y <= wallMaxY; y++) {
          level.setBlock(pos.set(minX + layer, y, z), barrier, flags);
          level.setBlock(pos.set(maxX - layer, y, z), barrier, flags);
        }
      }
    }

    for (int layer = 0; layer < 2; layer++) {
      for (int x = minX + 2; x <= maxX - 2; x++) {
        for (int y = wallMinY; y <= wallMaxY; y++) {
          level.setBlock(pos.set(x, y, minZ + layer), barrier, flags);
          level.setBlock(pos.set(x, y, maxZ - layer), barrier, flags);
        }
      }
    }
  }
}
