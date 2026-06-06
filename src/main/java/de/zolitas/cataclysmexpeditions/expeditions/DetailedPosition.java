package de.zolitas.cataclysmexpeditions.expeditions;

import org.jetbrains.annotations.NotNull;

public record DetailedPosition(float x, float y, float z, int yaw) {
  public static DetailedPosition fromString(String string) throws NumberFormatException {
    String[] segments = string.split(" ");
    return new DetailedPosition(
        Float.parseFloat(segments[0]),
        Float.parseFloat(segments[1]),
        Float.parseFloat(segments[2]),
        Integer.parseInt(segments[3])
    );
  }

  @Override
  public @NotNull String toString() {
    return x + " " + y + " " + z + " " + yaw;
  }
}
