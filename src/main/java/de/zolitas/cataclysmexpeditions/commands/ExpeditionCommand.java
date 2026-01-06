package de.zolitas.cataclysmexpeditions.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.zolitas.cataclysmexpeditions.CataclysmExpeditions;
import de.zolitas.cataclysmexpeditions.expeditions.ExpeditionException;
import de.zolitas.cataclysmexpeditions.expeditions.ExpeditionUtils;
import de.zolitas.cataclysmexpeditions.expeditions.Expeditions;
import de.zolitas.cataclysmexpeditions.world.ExpeditionWorldUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

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
                                      .stream(Expeditions.values())
                                      .map(Expeditions::getId)
                                      .forEach(builder::suggest);
                                  return builder.buildFuture();
                                }))
                                .then(
                                    Commands
                                        .argument("targets", EntityArgument.players())
                                        .executes(context -> {
                                          try {
                                            String expeditionId = StringArgumentType.getString(context, "expedition");
                                            Expeditions expedition = Expeditions.getById(expeditionId);
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
                                                context.getSource().registryAccess()
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
