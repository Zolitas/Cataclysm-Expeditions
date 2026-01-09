package de.zolitas.cataclysmexpeditions.network;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.blocks.ExpeditionAnchorBlock;
import de.zolitas.cataclysmexpeditions.network.payload.ConfirmAnchorPayload;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkRegister {
  public static void register(RegisterPayloadHandlersEvent event) {
    var registrar = event.registrar(CataclysmExpeditions.MODID);

    registrar.playToServer(
        ConfirmAnchorPayload.TYPE,
        ConfirmAnchorPayload.CODEC,
        (payload, context) -> {
          context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
              return;
            }

            if (player.isRemoved() || player.level().isClientSide()) {
              return;
            }

            ServerLevel level = player.serverLevel();
            BlockPos pos = payload.getAnchorPos();

            if (!player.blockPosition().closerThan(pos, 10.0)) {
              player.sendSystemMessage(Component.translatable("error.cataclysm_expeditions.expedition_anchor.too_far").withStyle(ChatFormatting.RED));
              return;
            }

            BlockState stateAtPos = level.getBlockState(pos);
            if (!(stateAtPos.getBlock() instanceof ExpeditionAnchorBlock)) {
              player.sendSystemMessage(Component.translatable("error.cataclysm_expeditions.expedition_anchor.not_anchor").withStyle(ChatFormatting.RED));
              return;
            }

            ExpeditionWorldUtils.teleportToHub(player);
          });
        }
    );
  }

  public static void sendToServer(CustomPacketPayload payload) {
    ClientPacketListener connection = Minecraft.getInstance().getConnection();
    if (connection != null) {
      connection.send(new ServerboundCustomPayloadPacket(payload));
    }
  }
}
