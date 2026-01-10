package de.zolitas.cataclysmexpeditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.zolitas.cataclysmexpeditions.expeditions.ExpeditionException;
import de.zolitas.cataclysmexpeditions.expeditions.ExpeditionUtils;
import de.zolitas.cataclysmexpeditions.expeditions.Expedition;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;

public class ExpeditionCommand {

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher
        .register(
            Commands
                .literal("expedition")
                .requires(stack -> stack.hasPermission(4))
                .then(
                    Commands
                        .literal("start")
                        .then(
                            Commands
                                .argument("expedition", StringArgumentType.word())
                                .suggests(((context, builder) -> {
                                  Arrays
                                      .stream(Expedition.values())
                                      .map(Expedition::getId)
                                      .forEach(builder::suggest);
                                  return builder.buildFuture();
                                }))
                                .then(
                                    Commands
                                        .argument("targets", EntityArgument.players())
                                        .executes(context -> {
                                          try {
                                            String expeditionId = StringArgumentType.getString(context, "expedition");
                                            Expedition expedition = Expedition.getById(expeditionId);
                                            if (expedition == null) {
                                              throw new ExpeditionException("Expedition not found!");
                                            }

                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            if (targets.isEmpty()) {
                                              throw new ExpeditionException("No player was found!");
                                            }

                                            ExpeditionUtils.startExpedition(
                                                expedition,
                                                targets,
                                                context.getSource().getServer(),
                                                context.getSource().registryAccess(),
                                                exception -> context.getSource().sendFailure(Component.literal(exception.getMessage()))
                                            );

                                            return 1;
                                          } catch (ExpeditionException exception) {
                                            context.getSource().sendFailure(Component.literal(exception.getMessage()));
                                            return 0;
                                          }
                                        })
                                )
                        )
                )
                .then(
                    Commands
                        .literal("tpToHub")
                        .then(
                            Commands
                                .argument("targets", EntityArgument.players())
                                .executes(context -> {
                                  for (ServerPlayer player : EntityArgument.getPlayers(context, "targets")) {
                                    ExpeditionWorldUtils.teleportToHub(player);
                                  }
                                  return 1;
                                })
                        )
                )
        );
  }
}
