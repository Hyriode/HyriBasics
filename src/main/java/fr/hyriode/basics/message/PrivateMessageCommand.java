package fr.hyriode.basics.message;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:24
 */
public class PrivateMessageCommand extends HyriCommand<HyriBasics> {

    public PrivateMessageCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("msg")
                .withAliases("message", "m", "dm", "pm", "tell")
                .withDescription("The command used to create a party and interact with it")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/msg <player> <message>"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "%player_online% %sentence%", output -> HyriBasics.get().getPrivateMessageModule().sendPrivateMessage(player, output.get(IHyriPlayer.class), output.get(String.class)));
    }

}
