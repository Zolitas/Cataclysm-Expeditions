package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.config.CMConfig;
import net.neoforged.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for CMConfig to modify config values on bake.
 * This ensures that respawning a boss is not possible.
 */
@Mixin(CMConfig.class)
public class CataclysmConfigMixin {
  @Inject(method = "bake", at = @At("TAIL"))
  private static void onBake(ModConfig config, CallbackInfo ci) {
    CMConfig.RemnantRespawner = false;
    CMConfig.EnderGuardianRespawner = false;
    CMConfig.HarbingerRespawner = false;
    CMConfig.ScyllaRespawner = false;
    CMConfig.MonstrosityRespawner = false;

    CMConfig.Cursed_tombstone_summon_cooldown = 1;
  }
}
