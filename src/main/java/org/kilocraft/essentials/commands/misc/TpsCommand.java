package org.kilocraft.essentials.commands.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.kilocraft.essentials.chat.ChatMessage;
import org.kilocraft.essentials.chat.KiloChat;
import org.kilocraft.essentials.util.TPSTracker;

import static net.minecraft.server.command.CommandManager.literal;

public class TpsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tps").executes(TpsCommand::run));
    }

    public static int run(CommandContext<ServerCommandSource> context) {

        KiloChat.sendMessageToSource(context.getSource(), new ChatMessage(
                String.format(
                        "&6TPS &8(&71m&8/&75m&8/&715m&8)&%s %s&8,&%s %s&8,&%s %s&r",
                        tpstoColorCode(TPSTracker.tps1.getAverage()),
                        TPSTracker.tps1.getShortAverage(),
                        tpstoColorCode(TPSTracker.tps5.getAverage()),
                        TPSTracker.tps5.getShortAverage(),
                        tpstoColorCode(TPSTracker.tps15.getAverage()),
                        TPSTracker.tps15.getShortAverage()
                ),
                true
        ));

        return 1;
    }

    private static char tpstoColorCode(double tps){
        if (tps > 15)
            return 'a';
        else if (tps > 10)
                return 'e';

        return 'c';
    }

}
