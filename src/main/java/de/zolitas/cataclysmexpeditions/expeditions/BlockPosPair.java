package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;

@Getter
@AllArgsConstructor
public class BlockPosPair {
  BlockPos firstPos;
  BlockPos secondPos;

  public static BlockPosPair fromString(String string) throws NumberFormatException {
    String[] parts = string.split(":");
    return new BlockPosPair(BlockPosUtils.fromString(parts[0]), BlockPosUtils.fromString(parts[1]));
  }

  @Override
  public String toString() {
    return BlockPosUtils.toString(firstPos) + ":" + BlockPosUtils.toString(secondPos);
  }
}
