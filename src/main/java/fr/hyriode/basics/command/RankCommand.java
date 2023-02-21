package fr.hyriode.basics.command;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class RankCommand extends HyriCommand<HyriBasics> {

    public RankCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("rank")
                .withDescription("Rank command")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/rank")
                .asynchronous()
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        this.handleArgument(ctx, "player %player% %input%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (player.getUniqueId().equals(targetId)) {
                return;
            }

            final PlayerRank rankType = this.getPlayerByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setPlayerType(rankType);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade joueur modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade joueur invalide!");
            }
        });

        this.handleArgument(ctx, "staff %player% reset", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            target.getRank().setStaffType(null);
            target.update();

            player.sendMessage(ChatColor.GREEN + "Grade staff reset!");
        });

        this.handleArgument(ctx, "staff %player% %input%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            final StaffRank rankType = this.getStaffByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setStaffType(rankType);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade staff modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade staff invalide!");
            }
        });
    }

    private StaffRank getStaffByName(String name) {
        for (StaffRank rankType : StaffRank.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

    private PlayerRank getPlayerByName(String name) {
        for (PlayerRank rankType : PlayerRank.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

}
