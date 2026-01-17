package de.zolitas.cataclysmexpeditions.config;

import com.mojang.serialization.MapCodec;
import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ConditionRegister {
  public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
      DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, CataclysmExpeditions.MODID);

  public static final Supplier<MapCodec<HubAnchorRecipeCondition>> HUB_ANCHOR_RECIPE_CONDITION =
      CONDITION_CODECS.register("hub_anchor_recipe_enabled", () -> HubAnchorRecipeCondition.CODEC);
}
