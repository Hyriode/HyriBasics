package fr.hyriode.basics.announcement;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 01/05/2022 at 20:10
 */
public class AnnouncementCommand extends HyriCommand<HyriBasics> {

    public AnnouncementCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("say")
                .withDescription("The command used to broadcast a message on each server.")
                .withAliases("bc", "broadcast", "annonce", "announce", "announcement")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/broadcast <message>")
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        this.handleArgument(ctx, "%sentence%", output -> {
            final Player player = (Player) ctx.getSender();

            HyriAPI.get().getNetworkManager().getEventBus().publish(new AnnouncementEvent(player.getUniqueId(), ChatColor.translateAlternateColorCodes('&', output.get(String.class).replace("\\n", "\n"))));
        });
    }

}
