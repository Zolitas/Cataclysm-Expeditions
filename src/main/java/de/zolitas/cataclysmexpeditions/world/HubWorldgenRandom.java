package de.zolitas.cataclysmexpeditions.world;

import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

/**
 * This WorldgenRandom is only meant to be used when generating the hub structure.
 * It removes some random features and instead return fixed values.
 * The main purpose of this class is that the hub is not randomly rotated when generated.
 */
public class HubWorldgenRandom extends WorldgenRandom {
  public HubWorldgenRandom() {
    super(new LegacyRandomSource(0));
  }

  @Override
  public int nextInt() {
    return 0;
  }

  @Override
  public int nextInt(int bound) {
    return 0;
  }
}
