package de.zolitas.cataclysmexpeditions.world;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.expeditions.DetailedPosition;
import de.zolitas.cataclysmexpeditions.expeditions.Expedition;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class ExpeditionWorldUtils {
  private static final ResourceLocation EXPEDITION_DIMENSION_LOCATION = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_world");
  private static final ResourceLocation EXPEDITION_DIMENSION_LOCATION_NETHER = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "expedition_world_nether");
  public static final ResourceLocation HUB_STRUCTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(CataclysmExpeditions.MODID, "hub");

  private static final Set<ChunkPos> hubChunks = Set.of(
      new ChunkPos(-99, -99),
      new ChunkPos(-99, -98),
      new ChunkPos(-100, -98),
      new ChunkPos(-100, -99)
  );

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

    generateHubStructure(server).whenComplete((unused, ex) -> {
      if (ex != null) {
        throw new RuntimeException("Failed to generate hub structure!", ex);
      }
      player.teleportTo(Objects.requireNonNull(getExpeditionLevel(server, false)), -1589.5, 1, -1594.5, 0, 0);
    });
  }

  private static CompletableFuture<Void> generateHubStructure(MinecraftServer server) {
    ServerLevel level = getExpeditionLevel(server, false);
    assert level != null;

    ExpeditionWorldSavedData expeditionWorldSavedData = getExpeditionWorldSavedData(level);

    if (expeditionWorldSavedData.isHubGenerated()) {
      return CompletableFuture.completedFuture(null);
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

    return placeStructure(level, chunkGenerator, structureStart)
        .thenRun(() -> {
          expeditionWorldSavedData.setHubGenerated(true);

          createLobbyDisplays(level);
        });
  }

  public static void createLobbyDisplays(ServerLevel level) {
    for (Expedition expedition : Expedition.values()) {
      Display.TextDisplay lobbyTextDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
      DetailedPosition uiPosition = expedition.getUiPosition();
      lobbyTextDisplay.setPos(uiPosition.getX(), uiPosition.getY(), uiPosition.getZ());
      lobbyTextDisplay.setYRot(uiPosition.getYaw());
      level.addFreshEntity(lobbyTextDisplay);

      getExpeditionWorldSavedData(level).setLobbyDisplay(expedition, lobbyTextDisplay.getStringUUID());
    }
  }

  public static CompletableFuture<Void> placeStructure(ServerLevel level, ChunkGenerator chunkGenerator, StructureStart structureStart) {
    return placeStructure(level, chunkGenerator, structureStart, null);
  }

  public static CompletableFuture<Void> placeStructure(ServerLevel level, ChunkGenerator chunkGenerator,
                                                       StructureStart structureStart, @Nullable BiConsumer<Integer, Integer> progressHandler) {
    BoundingBox boundingBox = structureStart.getBoundingBox();
    ChunkPos chunkPos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.minX()), SectionPos.blockToSectionCoord(boundingBox.minZ()));
    ChunkPos chunkPos2 = new ChunkPos(SectionPos.blockToSectionCoord(boundingBox.maxX()), SectionPos.blockToSectionCoord(boundingBox.maxZ()));

    List<ChunkPos> chunks = new ArrayList<>();
    ChunkPos.rangeClosed(chunkPos1, chunkPos2).forEach(chunks::add);

    CompletableFuture<Void> future = new CompletableFuture<>();
    MinecraftServer server = level.getServer();

    final int BATCH_SIZE = 2; // place a few chunks per tick to avoid long stalls
    final int[] index = {0};
    final int total = chunks.size();

    Runnable runBatch = new Runnable() {
      @Override
      public void run() {
        try {
          int placed = 0;
          while (index[0] < chunks.size() && placed < BATCH_SIZE) {
            ChunkPos chunkPos = chunks.get(index[0]);
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
            placed++;
            index[0]++;

            if (progressHandler != null) {
              progressHandler.accept(index[0], total);
            }
          }

          if (index[0] < chunks.size()) {
            // schedule next batch next tick
            server.execute(this);
          } else {
            future.complete(null);
          }
        } catch (Exception exception) {
          future.completeExceptionally(exception);
        }
      }
    };

    server.execute(runBatch);

    return future;
  }

  @SubscribeEvent
  private static void onLevelLoad(LevelEvent.Load event) {
    handleHubChunkForce(true, event.getLevel());
  }

  @SubscribeEvent
  private static void onLevelUnload(LevelEvent.Unload event) {
    handleHubChunkForce(false, event.getLevel());
  }

  private static void handleHubChunkForce(boolean add, LevelAccessor level) {
    if (level.isClientSide()) return;
    if (!(level instanceof ServerLevel serverLevel)) return;
    if (!serverLevel.dimension().location().equals(EXPEDITION_DIMENSION_LOCATION)) return;
    for (ChunkPos hubChunk : hubChunks) {
      serverLevel.setChunkForced(hubChunk.x, hubChunk.z, add);
    }
  }
}
