package de.zolitas.cataclysmexpeditions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin for ServerPlayer that lets players keep their inventory when dying in a expedition dimension
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
  @Redirect(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
  private boolean shouldKeepInventory(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, @Local(argsOnly = true) ServerPlayer that) {
    Level level = that.level();

    if (ExpeditionWorldUtils.isExpeditionDimension(level.dimension().location())) {
      return true;
    }

    return instance.getBoolean(key);
  }
}
