package de.zolitas.cataclysmexpeditions.client.screens;

import de.zolitas.cataclysmexpeditions.network.payload.ConfirmAnchorPayload;
import de.zolitas.cataclysmexpeditions.network.NetworkRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ExpeditionAnchorConfirmScreen extends ConfirmScreen {
  public ExpeditionAnchorConfirmScreen(BlockPos anchorPos) {
    super(
        (confirmed) -> {
          if (confirmed) {
            NetworkRegister.sendToServer(new ConfirmAnchorPayload(anchorPos));
          }
          Minecraft.getInstance().setScreen(null);
        },
        Component.translatable("gui.cataclysm_expeditions.expedition_anchor.confirm.title").withStyle(ChatFormatting.BOLD),
        Component.translatable("gui.cataclysm_expeditions.expedition_anchor.confirm.message")
    );
  }

  public static void createAndSetScreen(BlockPos anchorPos) {
    Minecraft.getInstance().setScreen(new ExpeditionAnchorConfirmScreen(anchorPos));
  }
}
