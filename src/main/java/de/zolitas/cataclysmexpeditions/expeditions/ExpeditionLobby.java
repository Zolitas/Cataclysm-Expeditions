package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.HashSet;
import java.util.Set;

public class ExpeditionLobby {
  public static final int TTL = 600;
  public static final int MAX_PLAYER_COUNT = 4;

  private final Expeditions expedition;

  @Getter
  @Setter
  private ExpeditionCallbackData callbackData = null;
  @Getter
  private int ttl = TTL;
  @Getter
  private boolean finishedGenerating = false;

  private final Set<ServerPlayer> players = new HashSet<>();
  private final ServerBossEvent bossBar = new ServerBossEvent(
      Component.translatable("gui.cataclysm_expeditions.expedition_loading"),
      BossEvent.BossBarColor.PURPLE,
      BossEvent.BossBarOverlay.PROGRESS
  );

  public ExpeditionLobby(Expeditions expedition) {
    this.expedition = expedition;
    bossBar.setProgress(0);
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
  }

  public void addPlayer(ServerPlayer player) {
    addPlayer(player, false);
  }

  public void addPlayer(ServerPlayer player, boolean teleportInstantly) {
    if (ttl <= 0) return;
    if (players.size() >= MAX_PLAYER_COUNT) return;
    if (players.contains(player)) return;

    bossBar.addPlayer(player);

    players.add(player);
    if (teleportInstantly) teleportPlayer(player);
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
