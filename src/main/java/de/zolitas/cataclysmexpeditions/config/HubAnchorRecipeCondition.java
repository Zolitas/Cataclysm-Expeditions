package de.zolitas.cataclysmexpeditions.config;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class HubAnchorRecipeCondition implements ICondition {
  public static final MapCodec<HubAnchorRecipeCondition> CODEC = MapCodec.unit(HubAnchorRecipeCondition::new);

  @Override
  public boolean test(@NotNull IContext context) {
    return CataclysmExpeditionsConfig.CONFIG.hubAnchorRecipeEnabled.get();
  }

  @Override
  public @NotNull MapCodec<? extends ICondition> codec() {
    return CODEC;
  }
}
