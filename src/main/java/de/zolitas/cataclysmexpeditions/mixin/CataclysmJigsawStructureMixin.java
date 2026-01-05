package de.zolitas.cataclysmexpeditions.mixin;

import com.github.L_Ender.cataclysm.structures.jisaw.CataclysmJigsawStructure;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CataclysmJigsawStructure.class)
public class CataclysmJigsawStructureMixin {
  @ModifyVariable(method = "findGenerationPoint", at = @At("STORE"), name = "startY")
  private int onHeightSample(int value) {
    Holder<StructureTemplatePool> startPool = ((CataclysmJigsawStructure) ((Object) this)).startPool;

    if (startPool.is(ResourceLocation.fromNamespaceAndPath("cataclysm", "frosted_prison/start_pool"))) {
      return 70;
    }
    else {
      return value;
    }
  }
}
