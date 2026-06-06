package de.zolitas.cataclysmexpeditions.expeditions;

import net.minecraft.core.BlockPos;

public class BlockPosUtils {
  public static String toString(BlockPos pos) {
    return String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ());
  }

  public static BlockPos fromString(String posString) throws NumberFormatException {
    String[] parts = posString.split(" ");
    return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
  }
}
