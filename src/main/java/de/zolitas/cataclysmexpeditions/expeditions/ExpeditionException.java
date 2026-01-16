package de.zolitas.cataclysmexpeditions.expeditions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;

@AllArgsConstructor
public class ExpeditionException extends Exception {
  @Getter
  private final Component component;
}
