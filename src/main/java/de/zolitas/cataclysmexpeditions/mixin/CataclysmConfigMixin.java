package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.config.CommonConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for CMConfig to modify config values on bake.
 * This ensures that respawning a boss is not possible.
 */
@Mixin(CMCommonConfig.class)
public class CataclysmConfigMixin {
  @Inject(method = "Commonbake", at = @At("TAIL"))
  private static void onBake(CommonConfig config, CallbackInfo ci) {
    CMCommonConfig.AncientRemnant.respawner = false;
    CMCommonConfig.EnderGuardian.respawner = false;
    CMCommonConfig.Harbinger.respawner = false;
    CMCommonConfig.Scylla.respawner = false;
    CMCommonConfig.NetheriteMonstrosity.respawner = false;

    CMCommonConfig.Blocks.CursedTombstoneCooldown = 1;
  }
}
