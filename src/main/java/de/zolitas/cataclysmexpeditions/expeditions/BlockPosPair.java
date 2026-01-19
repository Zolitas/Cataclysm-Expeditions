package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;

@Getter
@AllArgsConstructor
public class BlockPosPair {
  BlockPos firstPos;
  BlockPos secondPos;
}
