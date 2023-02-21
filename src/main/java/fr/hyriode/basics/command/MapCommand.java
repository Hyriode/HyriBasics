package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.utils.Symbols;
import fr.hyriode.hyrame.utils.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by AstFaster
 * on 21/02/2023 at 17:03
 */
public class MapCommand extends HyriCommand<HyriBasics> {

    public MapCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("map")
                .withAliases("carte", "world", "monde", "build", "builder", "builders")
                .withDescription("Display the information of the current map")
                .withUsage("/map"));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriServer server = HyriAPI.get().getServer();

        IHyriWorld world;
        if (server.getGameType() != null) {
            world = HyriAPI.get().getWorldManager().getWorld(server.getType(), server.getGameType(), Objects.requireNonNull(server.getMap()));
        } else {
            world = HyriAPI.get().getWorldManager().getWorld(server.getType(), Objects.requireNonNull(server.getMap()));
        }

        final ComponentBuilder builder = new ComponentBuilder(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + Symbols.HYPHENS_LINE + "\n")
                .append(BasicsMessage.COMMAND_MAP_MESSAGE.asString(player)
                        .replace("%name%", world.getName())
                        .replace("%creation_date%", TimeUtil.formatDate(new Date(world.getCreationDate()), "dd/MM/yyyy"))
                        .replace("%authors%", this.formatAuthors(world.getAuthors())))
                .append("\n");

        builder.append(ChatColor.DARK_AQUA + "" + ChatColor.STRIKETHROUGH + Symbols.HYPHENS_LINE);

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
