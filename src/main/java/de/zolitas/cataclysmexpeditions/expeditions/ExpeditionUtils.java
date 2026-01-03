package de.zolitas.cataclysmexpeditions.expeditions;

import com.mojang.brigadier.arguments.StringArgumentType;
import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ExpeditionUtils {
  public static @Nullable ServerLevel getExpeditionLevel(MinecraftServer server) {
    ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, CataclysmExpeditions.EXPEDITION_DIMENSION_LOCATION);
    return server.getLevel(levelKey);
  }

  public static void startExpedition(Expeditions expedition, Collection<ServerPlayer> targets, MinecraftServer server, RegistryAccess registryAccess)
      throws ExpeditionException {
    ServerLevel expeditionLevel = getExpeditionLevel(server);

    if (expeditionLevel == null) {
      throw new ExpeditionException("Expedition dimension not found!");
    }

    Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
    Structure structure = structureRegistry.get(expedition.getStructureLocation());

    if (structure == null) {
      throw new ExpeditionException("Expedition structure not found!");
    }

    placeExpeditionStructure(registryAccess, expeditionLevel, structure);


    for (ServerPlayer target : targets) {
      target.teleportTo(expeditionLevel, 0, 0, 0, 0, 0);
    }
  }

  private static void placeExpeditionStructure(RegistryAccess registryAccess, ServerLevel expeditionLevel, Structure structure) throws ExpeditionException {
    ChunkGenerator chunkGenerator = expeditionLevel.getChunkSource().getGenerator();
    StructureStart structureStart = structure.generate(
        registryAccess,
        chunkGenerator,
        chunkGenerator.getBiomeSource(),
        expeditionLevel.getChunkSource().randomState(),
        expeditionLevel.getStructureManager(),
        expeditionLevel.getSeed(),
        new ChunkPos(0, 0),
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
