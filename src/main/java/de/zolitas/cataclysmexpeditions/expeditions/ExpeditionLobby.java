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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Display;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class ExpeditionLobby {
  private final Expedition expedition;

  @Getter
  @Setter
  private ExpeditionCallbackData callbackData = null;
  @Getter
  private int duration = CataclysmExpeditionsConfig.CONFIG.expeditionLobbyDuration.get() * 20;
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
    lobbyTextDisplay = ExpeditionWorldUtils.getLobbyTextDisplay(expedition, server);
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

  public void decreaseDuration() {
    duration--;
    if (duration % 20 == 0) updateLobbyTextDisplay();
  }

  public boolean containsPlayer(ServerPlayer player) {
    return players.contains(player);
  }

  public void addPlayer(ServerPlayer player, boolean teleportInstantly) {
    if (duration <= 0) return;
    if (players.size() >= CataclysmExpeditionsConfig.CONFIG.maxExpeditionPlayerCount.get()) return;
    if (players.contains(player)) return;

    bossBar.addPlayer(player);

    players.add(player);

    player.setData(
        AttachmentTypesRegister.LAST_EXPEDITION_USES.get(expedition),
        Instant.now()
    );

    if (teleportInstantly) teleportPlayer(player);

    if (lobbyTextDisplay != null) {
      updateLobbyTextDisplay();
    }
  }

  public void removePlayer(ServerPlayer player) {
    if (duration <= 0) return;
    if (!players.contains(player)) return;

    players.remove(player);
    bossBar.removePlayer(player);

    if (CataclysmExpeditionsConfig.CONFIG.resetExpeditionCooldownWhenLeavingDuringLobby.get()) {
      player.setData(
          AttachmentTypesRegister.LAST_EXPEDITION_USES.get(expedition),
          Instant.MIN
      );
    }

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

    component.append(Component.literal("\n\u231B " + duration / 20 + "s").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xaa5bf0))));

    lobbyTextDisplay.setText(component);
  }

  public void teleportAllPlayers() {
    players.forEach(this::teleportPlayer);
  }

  private void teleportPlayer(ServerPlayer player) {
    if (callbackData == null) return;
    MinecraftServer server = player.getServer();
    if (server == null) return;

    server.execute(() ->
        player.teleportTo(
            callbackData.getLevel(),
            callbackData.getStartPos().getX() + expedition.getTeleportationOffset().x(),
            callbackData.getStartPos().getY() + expedition.getTeleportationOffset().y(),
            callbackData.getStartPos().getZ() + expedition.getTeleportationOffset().z(),
            expedition.getTeleportationOffset().yaw(),
            0
        ));
  }
}
