package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExpeditionUtils {
  public static void startExpedition(Expeditions expedition, Collection<ServerPlayer> targets,
                                     MinecraftServer server, RegistryAccess registryAccess, Consumer<ExpeditionException> exceptionHandler)
  {
    ServerLevel expeditionLevel = ExpeditionWorldUtils.getExpeditionLevel(server, expedition.isNether());

    if (expeditionLevel == null) {
      exceptionHandler.accept(new ExpeditionException("Expedition dimension not found!"));
      return;
    }

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure structure = structureRegistry.get(expedition.getStructureLocation());

    if (structure == null) {
      exceptionHandler.accept(new ExpeditionException("Expedition structure not found!"));
      return;
    }

    int expeditionCounter = ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).getExpeditionCounter();

    ChunkPos placementChunkPos = new ChunkPos((expeditionCounter % 100) * 100, (expeditionCounter / 100) * 100);

    // bossbar to indicate loading progressHandler
    ServerBossEvent bossBar = new ServerBossEvent(
        Component.translatable("gui.cataclysm_expeditions.expedition_loading"),
        BossEvent.BossBarColor.PURPLE,
        BossEvent.BossBarOverlay.PROGRESS
    );
    bossBar.setProgress(0);
    bossBar.setVisible(true);
    for (ServerPlayer player : targets) {
      bossBar.addPlayer(player);
    }

    BiConsumer<Integer, Integer> progressHandler = (placed, total) -> {
      try {
        bossBar.setProgress((float) placed / total);
      }
      catch (Exception exception) {
        bossBar.setProgress(0);
      }
    };

    placeExpeditionStructure(registryAccess, expeditionLevel, structure, placementChunkPos, progressHandler)
        .thenRun(() -> {
          bossBar.setProgress(1);
          bossBar.setVisible(false);
          bossBar.removeAllPlayers();

          ExpeditionWorldUtils.getExpeditionWorldSavedData(server, expedition.isNether()).incrementExpeditionCounter();

          ExpeditionCallbackData expeditionCallbackData = ExpeditionCallbackData.builder()
              .players(targets)
              .level(expeditionLevel)
              .startPos(new BlockPos(placementChunkPos.getMinBlockX(), 0, placementChunkPos.getMinBlockZ()))
              .build();

          expedition.getCallback().accept(expeditionCallbackData);
        })
        .exceptionally(exception -> {
          bossBar.setVisible(false);
          bossBar.removeAllPlayers();
          exceptionHandler.accept(new ExpeditionException(exception.getMessage()));
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
      return CompletableFuture.failedFuture(new ExpeditionException("Expedition structure could not be generated!"));
    }

    return ExpeditionWorldUtils.placeStructure(expeditionLevel, chunkGenerator, structureStart, progressHandler);
  }
}
