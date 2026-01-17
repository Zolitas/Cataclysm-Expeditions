package de.zolitas.cataclysmexpeditions.expeditions;

import de.zolitas.cataclysmexpeditions.config.CataclysmExpeditionsConfig;
import de.zolitas.cataclysmexpeditions.entities.AttachmentTypesRegister;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExpeditionLobby {
  private final Expedition expedition;

  @Getter
  @Setter
  private ExpeditionCallbackData callbackData = null;
  @Getter
  private int ttl = CataclysmExpeditionsConfig.CONFIG.expeditionLobbyTtl.get();
  @Getter
  private boolean finishedGenerating = false;

  private final Display.TextDisplay lobbyTextDisplay;

  private final Set<ServerPlayer> players = new HashSet<>();
  private final ServerBossEvent bossBar = new ServerBossEvent(
      Component.translatable("gui.cataclysm_expeditions.expedition_loading"),
      BossEvent.BossBarColor.PURPLE,
      BossEvent.BossBarOverlay.PROGRESS
  );

  public ExpeditionLobby(Expedition expedition, MinecraftServer server) {
    this.expedition = expedition;
    bossBar.setProgress(0);
    lobbyTextDisplay = getLobbyTextDisplay(expedition, server);
  }

  private static Display.TextDisplay getLobbyTextDisplay(Expedition expedition, MinecraftServer server) {
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

  public void removed() {
    bossBar.removeAllPlayers();
    bossBar.setVisible(false);

    if (lobbyTextDisplay != null) {
      lobbyTextDisplay.setText(Component.empty());
    }
  }

  public void finishedGenerating() {
    finishedGenerating = true;
    bossBar.removeAllPlayers();
    bossBar.setVisible(false);
  }

  public void setGenerationProgress(float progress) {
    bossBar.setProgress(progress);
  }

  public void decreaseTTL() {
    ttl--;
    if (ttl % 20 == 0) updateLobbyTextDisplay();
  }

  public void addPlayer(ServerPlayer player) {
    addPlayer(player, false);
  }

  public void addPlayer(ServerPlayer player, boolean teleportInstantly) {
    if (ttl <= 0) return;
    if (players.size() >= CataclysmExpeditionsConfig.CONFIG.maxExpeditionPlayerCount.get()) return;
    if (players.contains(player)) return;
    if (player.getData(AttachmentTypesRegister.EXPEDITION_COOLDOWNS.get(expedition)) > 0) return;

    bossBar.addPlayer(player);

    players.add(player);

    player.setData(
        AttachmentTypesRegister.EXPEDITION_COOLDOWNS.get(expedition),
        CataclysmExpeditionsConfig.CONFIG.expeditionCooldown.get()
    );

    if (teleportInstantly) teleportPlayer(player);

    if (lobbyTextDisplay != null) {
      updateLobbyTextDisplay();
    }
  }

  private void updateLobbyTextDisplay() {
    if (lobbyTextDisplay == null) return;

    MutableComponent component = Component.empty();

    boolean first = true;
    for (ServerPlayer player : players) {
      component.append((first ? "" : "\n") + player.getName().getString());
      first = false;
    }

    for (int i = 0; i < CataclysmExpeditionsConfig.CONFIG.maxExpeditionPlayerCount.get() - players.size(); i++) {
      component.append("\n - ");
    }

    component.append(Component.literal("\n\u231B " + ttl / 20 + "s").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xaa5bf0))));

    lobbyTextDisplay.setText(component);
  }

  public void teleportAllPlayers() {
    players.forEach(this::teleportPlayer);
  }

  private void teleportPlayer(ServerPlayer player) {
    if (callbackData == null) return;

    player.teleportTo(
        callbackData.getLevel(),
        callbackData.getStartPos().getX() + expedition.getTeleportationOffset().getX(),
        callbackData.getStartPos().getY() + expedition.getTeleportationOffset().getY(),
        callbackData.getStartPos().getZ() + expedition.getTeleportationOffset().getZ(),
        expedition.getTeleportationOffset().getYaw(),
        0
    );
  }
}
