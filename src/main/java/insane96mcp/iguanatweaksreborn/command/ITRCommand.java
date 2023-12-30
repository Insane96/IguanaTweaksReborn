package insane96mcp.iguanatweaksreborn.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness.TirednessHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

public class ITRCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("iguanatweaks").requires(source -> source.hasPermission(2))
                .then(Commands.literal("tiredness")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0))
                                                .executes(context -> TirednessHandler.setFromCommand(context.getSource(), EntityArgument.getPlayers(context, "players"), FloatArgumentType.getFloat(context, "amount")))))
                                .then(Commands.literal("reset")
                                        .executes(context -> TirednessHandler.setFromCommand(context.getSource(), EntityArgument.getPlayers(context, "players"), 0))))));
    }

}
