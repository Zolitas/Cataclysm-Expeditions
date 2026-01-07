package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.structures.Burning_Arena_Structure;
import com.github.L_Ender.cataclysm.structures.Sunken_City_Structure;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Burning_Arena_Structure.class)
public class BurningArenaStructureMixin {
  @Redirect(method = "generatePieces", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Rotation;getRandom(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/Rotation;"))
  private static Rotation onGetRandomRotation(RandomSource random) {
    return Rotation.NONE;
  }
}
