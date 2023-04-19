package fr.hyriode.basics.message;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:24
 */
public class PrivateMessageCommand extends HyriCommand<HyriBasics> {

    public PrivateMessageCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("msg")
                .withAliases("message", "m", "dm", "pm", "tell")
                .withDescription("The command used to create a party and interact with it")
                .withUsage(new CommandUsage().withStringMessage(player -> "/msg <player> <message>")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("%player_online% %sentence%", output -> HyriBasics.get().getPrivateMessageModule().sendPrivateMessage(player, output.get(IHyriPlayer.class), output.get(String.class)));

        super.handle(ctx);
    }

}
