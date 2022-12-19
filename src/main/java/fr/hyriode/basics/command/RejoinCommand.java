package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.reconnection.IHyriReconnectionData;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 26/05/2022 at 19:33
 */
public class RejoinCommand extends HyriCommand<HyriBasics> {

    public RejoinCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("rejoin")
                .withAliases("reconnect")
                .withDescription("Command used to rejoin a left game")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/rejoin"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriReconnectionData reconnectionData = HyriAPI.get().getServerManager().getReconnectionHandler().get(player.getUniqueId());

        if (reconnectionData != null) {
            player.sendMessage(BasicsMessage.COMMAND_REJOIN_ADDED.asString(player));

            reconnectionData.reconnect();
        } else {
            player.sendMessage(BasicsMessage.COMMAND_REJOIN_CANCEL.asString(player));
        }
    }

}
