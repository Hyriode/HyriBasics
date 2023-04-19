package fr.hyriode.basics.command.network;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.ILobbyAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class LobbyCommand extends HyriCommand<HyriBasics> {

    public LobbyCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("lobby")
                .withAliases("l", "hub")
                .withDescription("The command used to return to lobby")
                .withUsage(new CommandUsage().withStringMessage(player -> "/lobby")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        if (HyriAPI.get().getServer().getType().equalsIgnoreCase(ILobbyAPI.TYPE)) {
            player.sendMessage(BasicsMessage.COMMAND_LOBBY_MESSAGE.asString(player));
            return;
        }

        HyriAPI.get().getLobbyAPI().sendPlayerToLobby(player.getUniqueId());
    }

}
