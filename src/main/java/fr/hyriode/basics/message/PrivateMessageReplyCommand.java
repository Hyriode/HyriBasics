package fr.hyriode.basics.message;

import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:52
 */
public class PrivateMessageReplyCommand extends HyriCommand<HyriBasics> {

    public PrivateMessageReplyCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("reply")
                .withAliases("r", "respond", "repondre", "répondre")
                .withDescription("The command used to reply to your latest message")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/r <message>"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%sentence%", output -> HyriBasics.get().getPrivateMessageModule().replyToMessage(player, output.get(String.class)));
    }

}
