package de.zolitas.cataclysmexpeditions.commands;

import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CataclysmExpeditions.MODID)
public class CommandsRegister {
  @SubscribeEvent
  public static void registerCommands(RegisterCommandsEvent event) {
    ExpeditionCommand.register(event.getDispatcher());
  }
}
