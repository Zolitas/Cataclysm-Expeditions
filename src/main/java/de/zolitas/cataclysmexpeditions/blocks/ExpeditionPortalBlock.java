package de.zolitas.cataclysmexpeditions.blocks;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.entities.AttachmentTypesRegister;
import de.zolitas.cataclysmexpeditions.expeditions.*;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class ExpeditionPortalBlock extends Block {
  public static final EnumProperty<Expedition> EXPEDITION_PROPERTY = EnumProperty.create("expedition", Expedition.class);

  public ExpeditionPortalBlock() {
    super(
        Properties.of()
            .noCollission()
            .strength(-1.0F, 3600000.0F)
            .noLootTable()
    );
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
    builder.add(EXPEDITION_PROPERTY);
  }

  @Override
  protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
    if (entity instanceof ServerPlayer serverPlayer) {
      if (serverPlayer.isSpectator() || serverPlayer.getData(AttachmentTypesRegister.EXPEDITION_PORTAL_COOLDOWN) > 0) return;

      serverPlayer.setData(AttachmentTypesRegister.EXPEDITION_PORTAL_COOLDOWN, 10);

      Expedition expedition = state.getValue(EXPEDITION_PROPERTY);

      ExpeditionLobby lobby = ExpeditionLobbyUtils.getLobby(expedition);
      if (lobby != null) {
        lobby.addPlayer(serverPlayer, true);
        return;
      }

      int expeditionCooldown = serverPlayer.getData(AttachmentTypesRegister.EXPEDITION_COOLDOWNS.get(expedition));
      if (expeditionCooldown > 0) {
        serverPlayer.displayClientMessage(getExpeditionCooldownComponent(expeditionCooldown), true);
        return;
      }

      ExpeditionUtils.startExpedition(
          expedition,
          List.of(serverPlayer),
          serverPlayer.getServer(),
          serverPlayer.registryAccess(),
          exception -> {
            serverPlayer.sendSystemMessage(Component.literal(exception.getMessage()).withStyle(ChatFormatting.RED));
            ExpeditionWorldUtils.teleportToHub(serverPlayer);
          }
      );
    }
  }

  private static @NotNull MutableComponent getExpeditionCooldownComponent(int expeditionCooldown) {
    int totalSeconds = expeditionCooldown / 20;
    int expeditionCooldownHours = totalSeconds / 3600;
    int expeditionCooldownMinutes = (totalSeconds % 3600) / 60;
    int expeditionCooldownSeconds = totalSeconds % 60;

    MutableComponent cooldownComponent = Component
        .translatable("error.cataclysm_expeditions.expedition_cooldown")
        .withStyle(ChatFormatting.RED);

    if (expeditionCooldownHours > 0) {
      cooldownComponent.append(Component.literal(expeditionCooldownHours + "h "));
    }

    if (expeditionCooldownMinutes > 0 || expeditionCooldownHours > 0) {
      cooldownComponent.append(Component.literal(expeditionCooldownMinutes + "m "));
    }

    cooldownComponent.append(Component.literal(expeditionCooldownSeconds + "s"));
    return cooldownComponent;
  }

  @SubscribeEvent
  private static void onPlayerTick(PlayerTickEvent.Post event) {
    Player player = event.getEntity();

    if (player.level().isClientSide()) return;

    if (player.getInBlockState().getBlock() instanceof ExpeditionPortalBlock) return;

    int cd = player.getData(AttachmentTypesRegister.EXPEDITION_PORTAL_COOLDOWN);
    if (cd > 0) {
      player.setData(AttachmentTypesRegister.EXPEDITION_PORTAL_COOLDOWN, cd - 1);
    }
  }
}
