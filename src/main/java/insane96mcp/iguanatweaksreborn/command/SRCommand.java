package insane96mcp.iguanatweaksreborn.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.TirednessHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class SRCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sr").requires(source -> source.hasPermission(2))
                .then(Commands.literal("tiredness")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                                .executes(context -> TirednessHandler.setFromCommand(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "amount")))))
                                .then(Commands.literal("reset")
                                        .executes(context -> TirednessHandler.setFromCommand(context.getSource(), EntityArgument.getPlayers(context, "players"), 0))))));
        /*dispatcher.register(Commands.literal("sr").requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("tiredness", EntityArgument.player())
                                .then(Commands.literal("get")
                                        .then(Commands.literal("wither")
                                                .executes(context -> getBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), "wither"))
                                        )
                                        .then(Commands.literal("dragon")
                                                .executes(context -> getBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), "dragon"))
                                        )
                                        .executes(context -> getBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), ""))
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.literal("wither")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.wither.feature.DifficultyFeature.maxDifficulty))
                                                        .executes(context -> setBossDifficulty(context.getSource(),EntityArgument.getPlayer(context, "targetPlayer"), "wither", IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                        )
                                        .then(Commands.literal("dragon")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.dragon.feature.DifficultyFeature.maxDifficulty))
                                                        .executes(context -> setBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), "dragon", IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.literal("wither")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.wither.feature.DifficultyFeature.maxDifficulty))
                                                        .executes(context -> addBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), "wither", IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                        )
                                        .then(Commands.literal("dragon")
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.dragon.feature.DifficultyFeature.maxDifficulty))
                                                        .executes(context -> addBossDifficulty(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"),"dragon", IntegerArgumentType.getInteger(context, "amount")))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("summon")
                        .then(Commands.literal(Strings.Tags.WITHER_MINION)
                                .then(Commands.argument("difficulty", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.wither.feature.DifficultyFeature.maxDifficulty))
                                        .executes(context -> summon(context.getSource(), Strings.Tags.WITHER_MINION, IntegerArgumentType.getInteger(context, "difficulty")))
                                )
                                .executes(context -> summon(context.getSource(), context.getSource().getPlayerOrException(), Strings.Tags.WITHER_MINION))
                        )
                        .then(Commands.literal(Strings.Tags.DRAGON_MINION)
                                .then(Commands.argument("difficulty", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.dragon.feature.DifficultyFeature.maxDifficulty))
                                        .executes(context -> summon(context.getSource(), Strings.Tags.DRAGON_MINION, IntegerArgumentType.getInteger(context, "difficulty")))
                                )
                                .executes(context -> summon(context.getSource(), context.getSource().getPlayerOrException(), Strings.Tags.DRAGON_MINION))
                        )
                        .then(Commands.literal(Strings.Tags.DRAGON_LARVA)
                                .then(Commands.argument("difficulty", IntegerArgumentType.integer(0, insane96mcp.progressivebosses.module.dragon.feature.DifficultyFeature.maxDifficulty))
                                        .executes(context -> summon(context.getSource(), Strings.Tags.DRAGON_LARVA, IntegerArgumentType.getInteger(context, "difficulty")))
                                )
                                .executes(context -> summon(context.getSource(), context.getSource().getPlayerOrException(), Strings.Tags.DRAGON_LARVA))
                        )
                        .then(Commands.literal(Strings.Tags.ELDER_MINION)
                                .executes(context -> summon(context.getSource(), context.getSource().getPlayerOrException(), Strings.Tags.ELDER_MINION))
                        )
                )
        );*/
    }

}
