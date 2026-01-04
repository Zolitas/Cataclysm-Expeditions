package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

@Data
@Builder
public class ExpeditionCallbackData {
  Collection<ServerPlayer> players;
  BlockPos startPos;
  ServerLevel level;
}
