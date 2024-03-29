package fr.hyriode.basics.command;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.utils.TimeUtil;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by AstFaster
 * on 21/02/2023 at 17:03
 */
public class MapCommand extends HyriCommand<HyriBasics> {

    public MapCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("map")
                .withAliases("carte", "world", "monde", "build", "builder", "builders")
                .withDescription("Display the information of the current map")
                .withUsage(new CommandUsage().withStringMessage(player -> "/map")));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final IHyriServer server = HyriAPI.get().getServer();

        IHyriWorld world;
        if (server.getGameType() != null) {
            world = HyriAPI.get().getWorldManager().getWorld(server.getType(), server.getGameType(), Objects.requireNonNull(server.getMap()));
        } else {
            world = HyriAPI.get().getWorldManager().getWorld(server.getType(), Objects.requireNonNull(server.getMap()));
        }

        player.sendMessage(BasicsMessage.COMMAND_MAP_MESSAGE.asString(player)
                .replace("%name%", world.getName())
                .replace("%creation_date%", TimeUtil.formatDate(new Date(world.getCreationDate()), "dd/MM/yyyy"))
                .replace("%builders%", this.formatAuthors(world.getAuthors())));
    }

    private String formatAuthors(List<String> authors) {
        final StringBuilder builder = new StringBuilder();

        if (authors.size() == 0) {
            return "-";
        }

        for (String author : authors) {
            builder.append(author).append(", ");
        }
        return builder.substring(0, builder.toString().length() - 2);
    }

}
