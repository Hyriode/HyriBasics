package fr.hyriode.basics.command.help;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class HelpCommand extends HyriCommand<HyriBasics> {

    public HelpCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("help")
                .withAliases("?", "aide")
                .withDescription("Command used to help you")
                .withUsage(new CommandUsage().withStringMessage(player -> "/help")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true)
                .append("\n\n").strikethrough(false)
                .append(this.getCommandLine(player, "discord")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/discord"))
                .append(this.getCommandLine(player, "website")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/website"))
                .append(this.getCommandLine(player, "store")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/store"))
                .append(this.getCommandLine(player, "friend")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend"))
                .append(this.getCommandLine(player, "party")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party"))
                .append(this.getCommandLine(player, "lobby")).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lobby"))
                .append(this.getCommandLine(player, "report")).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/report "))
                .append(this.getCommandLine(player, "map")).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/map"))
                .append("\n")
                .event((ClickEvent) null)
                .append(Symbols.HYPHENS_LINE).color(ChatColor.DARK_AQUA).strikethrough(true);

        player.spigot().sendMessage(builder.create());
    }

    private String getCommandLine(Player player, String command) {
        return ChatColor.AQUA + " /" + command + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + HyriLanguageMessage.get("command.help." + command).getValue(player) + "\n";
    }

}
