package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.structures.Cursed_Pyramid_Structure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cursed_Pyramid_Structure.class)
public class CursedPyramidStructureMixin {
  @Inject(method = "posToSurface", at = @At("RETURN"), cancellable = true)
  private static void onPosToSurface(ChunkGenerator generator, BlockPos pos, LevelHeightAccessor heightAccessor, RandomState state, CallbackInfoReturnable<BlockPos> cir) {
    cir.setReturnValue(cir.getReturnValue().atY(heightAccessor.getMinBuildHeight() + 70));
  }

  @ModifyVariable(method = "generatePieces", at = @At("STORE"), name = "rotation")
  private static Rotation onGetRandomRotation(Rotation value) {
    return Rotation.NONE;
  }
}
