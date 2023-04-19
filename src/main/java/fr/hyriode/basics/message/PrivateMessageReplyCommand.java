package fr.hyriode.basics.message;

import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:52
 */
public class PrivateMessageReplyCommand extends HyriCommand<HyriBasics> {

    public PrivateMessageReplyCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("reply")
                .withAliases("r", "respond", "repondre", "répondre")
                .withDescription("The command used to reply to your latest message")
                .withUsage(new CommandUsage().withStringMessage(player -> "/r <message>")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("%sentence%", output -> HyriBasics.get().getPrivateMessageModule().replyToMessage(player, output.get(String.class)));

        super.handle(ctx);
    }

}
