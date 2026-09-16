package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

@Data
@Builder
public class ExpeditionCallbackData {
  BlockPos startPos;
  ServerLevel level;
}
