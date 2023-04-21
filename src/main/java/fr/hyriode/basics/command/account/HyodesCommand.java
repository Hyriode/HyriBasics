package fr.hyriode.basics.command.account;

import fr.hyriode.api.player.IHyriPlayer;
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
public class HyodesCommand extends HyriCommand<HyriBasics> {

    public HyodesCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("hyodes")
                .withDescription("Hyodes command")
                .withUsage(new CommandUsage().withStringMessage(player -> "/hyodes [add|remove|set] <player> <amount>"))
                .asynchronous()
                .withPermission(player -> player.getRank().is(StaffRank.ADMINISTRATOR)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();

        ctx.registerArgument("add %player% %long%", "/hyodes add <player> <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyodes().add(amount).exec();
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyodes du joueur modifié!");
        });

        ctx.registerArgument("remove %player% %long%", "/hyodes <player> remove <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyodes().remove(amount).exec();
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyodes du joueur modifié!");
        });

        ctx.registerArgument("set %player% %long%", "/hyodes <player> set <amount>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final long amount = output.get(Long.class);

            target.getHyodes().setAmount(amount);
            target.update();

            player.sendMessage(ChatColor.GREEN + "Hyodes du joueur modifié!");
        });

        super.handle(ctx);
    }

}
