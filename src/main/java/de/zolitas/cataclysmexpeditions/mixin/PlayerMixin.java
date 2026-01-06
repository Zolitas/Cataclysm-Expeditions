package de.zolitas.cataclysmexpeditions.mixin;

import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin for Player that lets players keep their inventory when dying in a expedition dimension
 */
@Mixin(Player.class)
public class PlayerMixin {
  @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
  private boolean shouldKeepInventory(GameRules instance, GameRules.Key<GameRules.BooleanValue> key) {
    Level level = ((Player) (Object) this).level();

    if (ExpeditionWorldUtils.isExpeditionDimension(level.dimension().location())) {
      return true;
    }

    return instance.getBoolean(key);
  }

  @Redirect(method = "getBaseExperienceReward", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
  private boolean shouldKeepXP(GameRules instance, GameRules.Key<GameRules.BooleanValue> key) {
    Level level = ((Player) (Object) this).level();

    if (ExpeditionWorldUtils.isExpeditionDimension(level.dimension().location())) {
      return true;
    }

    return instance.getBoolean(key);
  }
}
