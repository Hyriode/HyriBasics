package fr.hyriode.basics.command.help;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class DiscordCommand extends HyriCommand<HyriBasics> {

    public DiscordCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("discord")
                .withAliases("support")
                .withType(HyriCommandType.PLAYER)
                .withDescription("Command used to get the discord link")
                .withUsage("/discord"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        player.spigot().sendMessage(new ComponentBuilder(BasicsMessage.COMMAND_DISCORD_MESSAGE.asString(player).replace("%discord%", HyriConstants.DISCORD_URL))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, HyriConstants.DISCORD_URL))
                .create());
    }

}
