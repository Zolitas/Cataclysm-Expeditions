package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class ExpeditionLobbyUtils {
  private static final Map<Expedition, ExpeditionLobby> expeditionLobbies = new HashMap<>();

  public static ExpeditionLobby createLobby(Expedition expedition, MinecraftServer server) {
    ExpeditionLobby existingLobby = getLobby(expedition);
    if (existingLobby != null) return existingLobby;

    ExpeditionLobby lobby = new ExpeditionLobby(expedition, server);
    expeditionLobbies.put(expedition, lobby);
    return lobby;
  }

  public static void deleteLobby(Expedition expedition) {
    ExpeditionLobby removedLobby = expeditionLobbies.remove(expedition);
    if (removedLobby != null) removedLobby.removed();
  }

  public static @Nullable ExpeditionLobby getLobby(Expedition expedition) {
    return expeditionLobbies.get(expedition);
  }

  @SubscribeEvent
  private static void onServerTick(ServerTickEvent.Post event) {
    Set<Expedition> expeditionsToRemove = new HashSet<>();

    expeditionLobbies.forEach((expedition, expeditionLobby) -> {
      if (!expeditionLobby.isFinishedGenerating()) return;

      expeditionLobby.decreaseTTL();

      if (expeditionLobby.getTtl() <= 0) {
        expeditionsToRemove.add(expedition);
      }
    });

    expeditionsToRemove.forEach(ExpeditionLobbyUtils::deleteLobby);
  }
}
