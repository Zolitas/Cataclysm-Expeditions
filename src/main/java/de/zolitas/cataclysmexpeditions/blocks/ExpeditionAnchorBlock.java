package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ExpeditionAnchorBlock extends Block {
  private static final VoxelShape COLLISION_SHAPE = Shapes.or(
      Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
      Block.box(3.0, 2.0, 3.0, 13.0, 3.0, 13.0),
      Block.box(5.0, 3.0, 5.0, 11.0, 11.0, 11.0),
      Block.box(2.0, 11.0, 2.0, 14.0, 13.0, 14.0),
      Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0),
      Block.box(1.0, 16.0, 1.0, 15.0, 17.0, 15.0)
  );

  public ExpeditionAnchorBlock() {
    super(
        Properties.of()
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
            .lightLevel((state) -> 5)
            .noOcclusion()
    );
  }

  @Override
  protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return COLLISION_SHAPE;
  }

  @Override
  protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
    return COLLISION_SHAPE;
  }

  @Override
  public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    super.animateTick(state, level, pos, random);

    double offsetX = (random.nextDouble() - 0.5) * 0.5;
    double offsetZ = (random.nextDouble() - 0.5) * 0.5;

    double exclusionRadius = 0.15;
    if (Math.abs(offsetX) < exclusionRadius && Math.abs(offsetZ) < exclusionRadius) {
      return;
    }

    double x = pos.getX() + 0.5 + offsetX;
    double y = pos.getY() + 1.05;
    double z = pos.getZ() + 0.5 + offsetZ;

    level.addParticle(
        new DustParticleOptions(new Vector3f(0.85f, 0.95f, 1f), 1.0f),
        x, y, z,
        0.0, 0.03, 0.0
    );
  }

  @Override
  protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                     @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
    if (player instanceof ServerPlayer serverPlayer) {
      ExpeditionWorldUtils.teleportToHub(serverPlayer);
    }

    player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

    return ItemInteractionResult.SUCCESS;
  }
}
