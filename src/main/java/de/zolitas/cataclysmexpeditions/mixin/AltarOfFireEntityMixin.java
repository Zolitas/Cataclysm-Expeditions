package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for AltarOfFire_Block_Entity to handle the removal of the altar after summoning the boss.
 * This ensures that respawning a boss is not possible.
 */
@Mixin(AltarOfFire_Block_Entity.class)
public class AltarOfFireEntityMixin {
  @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
  private void afterRemovalOfItemAndSpawningOfBoss(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
    level.removeBlock(pos, false);
  }
}
