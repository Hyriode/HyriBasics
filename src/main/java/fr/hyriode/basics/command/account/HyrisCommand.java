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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class HyrisCommand extends HyriCommand<HyriBasics> {

    public HyrisCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("hyris")
                .withDescription("Hyris command")
                .withUsage(new CommandUsage().withStringMessage(player -> "/hyris [add|remove|set] <player> <amount>"))
                .asynchronous()
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("add %player% %long%", "/hyris add <player> <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyris().add(amount).withMultiplier(false).exec();
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyris du joueur modifié!");
        });

        ctx.registerArgument("remove %player% %long%", "/hyris remove <player> <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyris().remove(amount).exec();
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyris du joueur modifié!");
        });

        ctx.registerArgument("set %player% %long%", "/hyris set <player> <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyris().setAmount(amount);
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyris du joueur modifié!");
        });

        super.handle(ctx);
    }

}
