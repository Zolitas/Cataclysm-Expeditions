package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin for Cursed_tombstone_Entity to force the summon cooldown to 0.
 * This ensures that the tombstone never gets reactivated and respawning the boss is not possible.
 */
@Mixin(Cursed_tombstone_Entity.class)
public class CursedTombstoneEntityMixin {
  @Redirect(method = "commonTick", at = @At(value = "FIELD", target = "Lcom/github/L_Ender/cataclysm/blockentities/Cursed_tombstone_Entity;summonCooldownProgress:I", opcode = Opcodes.GETFIELD))
  private static int onGetSummonCooldownProgress(Cursed_tombstone_Entity instance) {
    return 0;
  }
}
