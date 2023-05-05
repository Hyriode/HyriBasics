package fr.hyriode.basics.command.account;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.event.RankUpdatedEvent;
import fr.hyriode.api.player.model.IHyriPlus;
import fr.hyriode.api.player.transaction.HyriPlusTransaction;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.api.rank.StaffRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class RankCommand extends HyriCommand<HyriBasics> {

    public RankCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("rank")
                .withDescription("Rank command")
                .withUsage(new CommandUsage().withStringMessage(player -> "/rank"))
                .asynchronous()
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("player %player% %input%", "/rank staff <player> <rank>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final PlayerRank rankType = this.getPlayerByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setPlayerType(rankType);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade joueur modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade joueur invalide!");
            }
        });

        ctx.registerArgument("staff %player% reset", "/rank staff <player> reset", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            target.getRank().setStaffType(null);
            target.update();

            player.sendMessage(ChatColor.GREEN + "Grade staff reset!");
        });

        ctx.registerArgument("staff %player% %input%", "/rank staff <player> <rank>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final StaffRank rankType = this.getStaffByName(output.get(String.class));

            if (rankType != null) {
                target.getRank().setStaffType(rankType);
                target.update();

                player.sendMessage(ChatColor.GREEN + "Grade staff modifié!");
            } else {
                player.sendMessage(ChatColor.RED + "Grade staff invalide!");
            }
        });

        ctx.registerArgument("hyri+ %player% %long%", "/rank hyri+ <player> <days>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long days = output.get(Long.class);
            final IHyriPlus hyriPlus = target.getHyriPlus();
            final boolean expired = hyriPlus.hasExpire();
            final long duration = days * 24 * 3600 * 1000;

            hyriPlus.setDuration(hyriPlus.getDuration() + duration);

            if (expired) {
                hyriPlus.enable();
            }

            target.getTransactions().add(HyriPlusTransaction.TRANSACTIONS_TYPE, new HyriPlusTransaction(duration));
            target.update();

            HyriAPI.get().getEventBus().publish(new RankUpdatedEvent(target.getUniqueId()));

            player.sendMessage(ChatColor.GREEN + "Hyri+ modifié!");
        });

        super.handle(ctx);
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
