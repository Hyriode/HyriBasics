package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class PingCommand extends HyriCommand<HyriBasics> {

    public PingCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("ping")
                .withAliases("lag", "latence", "latency", "ms")
                .withDescription("The command used to check your latency")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/ping"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        player.sendMessage(BasicsMessage.COMMAND_PING_MESSAGE.asString(player).replace("%ping%", String.valueOf(HyriAPI.get().getPlayerManager().getPing(player.getUniqueId()))));
    }

}
