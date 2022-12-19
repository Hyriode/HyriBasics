package fr.hyriode.basics.command;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.api.rank.type.HyriStaffRankType;
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
                .withPermission(player -> player.getRank().is(HyriStaffRankType.ADMINISTRATOR)));
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

            final HyriPlayerRankType rankType = this.getPlayerByName(output.get(String.class));

            if (rankType != null) {
                target.setPlayerRank(rankType);
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

            target.setStaffRank(null);
            target.update();

            player.sendMessage(ChatColor.GREEN + "Grade staff reset!");
        });

        this.handleArgument(ctx, "staff %player% %input%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (player.getUniqueId().equals(target.getUniqueId())) {
                return;
            }

            final HyriStaffRankType rankType = this.getStaffByName(output.get(String.class));

            if (rankType != null) {
                target.setStaffRank(rankType);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade staff modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade staff invalide!");
            }
        });
    }

    private HyriStaffRankType getStaffByName(String name) {
        for (HyriStaffRankType rankType : HyriStaffRankType.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

    private HyriPlayerRankType getPlayerByName(String name) {
        for (HyriPlayerRankType rankType : HyriPlayerRankType.values()) {
            if (rankType.getName().equals(name)) {
                return rankType;
            }
        }
        return null;
    }

}
