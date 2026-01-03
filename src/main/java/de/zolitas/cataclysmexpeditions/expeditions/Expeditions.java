package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@AllArgsConstructor
public enum Expeditions {
  NETHERITE_MONSTROSITY("netherite_monstrosity", ResourceLocation.fromNamespaceAndPath("cataclysm", "soul_black_smith"));

  @Getter
  private final String id;

  @Getter
  private final ResourceLocation structureLocation;

  public static @Nullable Expeditions getById(String id) {
    return Arrays
        .stream(Expeditions.values())
        .filter(expedition -> expedition.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
