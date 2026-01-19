package de.zolitas.cataclysmexpeditions.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class VoidDeathBlock extends Block {
  public VoidDeathBlock() {
    super(
        Properties.of()
            .noLootTable()
            .noCollission()
            .strength(-1.0F, 3600000.0F)
            .noTerrainParticles()
            .forceSolidOn()
    );
  }

  @Override
  protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
    if (!(entity instanceof ServerPlayer serverPlayer)) return;

    serverPlayer.hurt(serverPlayer.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
  }
}
