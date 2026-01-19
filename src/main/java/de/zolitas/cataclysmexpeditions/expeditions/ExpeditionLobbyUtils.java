package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.entities.AttachmentTypesRegister;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.time.Duration;
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

  public static Collection<ExpeditionLobby> getLobbies() {
    return expeditionLobbies.values();
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

  public static void clearAllLobbyDisplays(MinecraftServer server) {
    for (Expedition expedition : Expedition.values()) {
      Display.TextDisplay textDisplay = getLobbyTextDisplay(expedition, server);
      if (textDisplay == null) continue;
      textDisplay.setText(Component.empty());
    }
  }

  @SubscribeEvent
  private static void onPlayerTick(PlayerTickEvent.Post event) {
    Player player = event.getEntity();

    if (player.level().isClientSide()) return;

    AttachmentTypesRegister.EXPEDITION_COOLDOWNS.forEach((expedition, cooldown) -> {
      int cdValue = player.getData(cooldown);
      if (cdValue > 0) {
        player.setData(cooldown, cdValue - 1);
      }
    });
  }

  @SubscribeEvent
  private static void onServerStarted(ServerStartedEvent event) {
    // i am so sorry for anyone reading this code
    new Thread(){
      @Override
      public void run() {
        try {
          Thread.sleep(Duration.ofSeconds(2));
        } catch (InterruptedException exception) {
          //ignored
        }
        clearAllLobbyDisplays(event.getServer());
      }
    }.start();
  }

  public static Display.TextDisplay getLobbyTextDisplay(Expedition expedition, MinecraftServer server) {
    ServerLevel expeditionLevel = ExpeditionWorldUtils.getExpeditionLevel(server, false);
    assert expeditionLevel != null;
    String lobbyDisplayUUID = ExpeditionWorldUtils.getExpeditionWorldSavedData(expeditionLevel).getLobbyDisplayUUID(expedition);
    if (lobbyDisplayUUID == null) return null;
    Entity lobbyDisplay = expeditionLevel.getEntity(UUID.fromString(lobbyDisplayUUID));

    if (!(lobbyDisplay instanceof Display.TextDisplay textDisplay)) {
      return null;
    }
    return textDisplay;
  }
}
