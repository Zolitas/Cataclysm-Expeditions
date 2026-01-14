package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.expeditions.ExpeditionUtils;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class HubAnchorBlock extends ExpeditionAnchorBlock {
  public HubAnchorBlock() {
    super(
        Properties.of()
            .lightLevel(value -> 5)
            .noOcclusion()
    );
  }

  @Override
  protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
    if (player instanceof ServerPlayer serverPlayer)
    {
      ExpeditionWorldUtils.teleportToHub(serverPlayer);
    }

    return ItemInteractionResult.SUCCESS;
  }
}
