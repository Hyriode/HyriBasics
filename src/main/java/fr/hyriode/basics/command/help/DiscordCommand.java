package fr.hyriode.basics.command.help;

import fr.hyriode.api.HyriConstants;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
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
        super(plugin, new CommandInfo("discord")
                .withAliases("support")
                .withDescription("Command used to get the discord link")
                .withUsage(new CommandUsage().withStringMessage(player -> "/discord")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        player.spigot().sendMessage(new ComponentBuilder(BasicsMessage.COMMAND_DISCORD_MESSAGE.asString(player).replace("%discord%", HyriConstants.DISCORD_URL))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, HyriConstants.DISCORD_URL))
                .create());
    }

}
