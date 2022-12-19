package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 15:50
 */
public class PluginsCommand extends HyriCommand<HyriBasics> {

    public PluginsCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("plugins")
                .withDescription("Show all plugins running on the server")
                .withAliases("pl", "ver", "version", "about", "icanhasbukkit", "spigot", "bukkit")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/plugins"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final ComponentBuilder builder = new ComponentBuilder(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + Symbols.HYPHENS_LINE + "\n")
                .append(BasicsMessage.COMMAND_PLUGINS_MESSAGE.asString(player)
                        .replace("%server%", HyriAPI.get().getServer().getName())
                        .replace("%version%", Bukkit.getVersion()))
                .append("\n");

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            final PluginDescriptionFile description = plugin.getDescription();
            final BaseComponent[] hover = TextComponent.fromLegacyText(
                    BasicsMessage.COMMAND_PLUGINS_HOVER_NAME.asString(player).replace("%name%", description.getName()) + "\n" +
                    BasicsMessage.COMMAND_PLUGINS_HOVER_VERSION.asString(player).replace("%version%", description.getVersion()) + "\n" +
                    BasicsMessage.COMMAND_PLUGINS_HOVER_AUTHORS.asString(player).replace("%authors%", this.formatAuthors(description.getAuthors())));

            builder.append(" " + ChatColor.DARK_GRAY + Symbols.DOT_BOLD + " " + ChatColor.WHITE + description.getName() + ChatColor.DARK_GRAY + " (v" + description.getVersion() + ")\n")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        }

        builder.append(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + Symbols.HYPHENS_LINE + "\n");

        player.spigot().sendMessage(builder.create());
    }

    private String formatAuthors(List<String> authors) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (authors.size() == 0) {
            return "-";
        }

        for (String author : authors) {
            stringBuilder.append(author).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
    }

}
