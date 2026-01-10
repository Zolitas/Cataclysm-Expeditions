package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class ExpeditionLobbyUtils {
  private static final Map<Expeditions, ExpeditionLobby> expeditionLobbies = new HashMap<>();

  public static ExpeditionLobby createLobby(Expeditions expedition) {
    ExpeditionLobby existingLobby = getLobby(expedition);
    if (existingLobby != null) return existingLobby;

    ExpeditionLobby lobby = new ExpeditionLobby(expedition);
    expeditionLobbies.put(expedition, lobby);
    return lobby;
  }

  public static void deleteLobby(Expeditions expedition) {
    expeditionLobbies.remove(expedition);
  }

  public static @Nullable ExpeditionLobby getLobby(Expeditions expedition) {
    return expeditionLobbies.get(expedition);
  }

  @SubscribeEvent
  private static void onServerTick(ServerTickEvent.Post event) {
    Set<Expeditions> expeditionsToRemove = new HashSet<>();

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
