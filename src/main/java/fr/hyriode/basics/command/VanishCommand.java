package fr.hyriode.basics.command;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class VanishCommand extends HyriCommand<HyriBasics> {

    public VanishCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("vanish")
                .withDescription("The command used to vanish yourself from other players")
                .withUsage(new CommandUsage().withStringMessage(player -> "/vanish"))
                .withPermission(player -> player.getHyriPlus().has()));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final HyriGame<?> currentGame = this.plugin.getHyrame().getGameManager().getCurrentGame();

        if (currentGame != null) {
            player.sendMessage(BasicsMessage.COMMAND_VANISH_GAME.asString(account));
            return;
        }

        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());

        if (session.isModerating()) {
            return;
        }

        if (session.isVanished()) {
            session.setVanished(false);

            for (Player target : Bukkit.getOnlinePlayers()) {
                final IHyriPlayerSession targetSession = IHyriPlayerSession.get(target.getUniqueId());

                if (!targetSession.isVanished() || targetSession.isModerating()) {
                    target.showPlayer(player);
                }
            }

            player.sendMessage(BasicsMessage.COMMAND_VANISH_UNSET.asString(account));
        } else {
            session.setVanished(true);

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target == player) {
                    continue;
                }

                final IHyriPlayer targetAccount = IHyriPlayer.get(target.getUniqueId());

                if (!targetAccount.getRank().isStaff()) {
                    target.hidePlayer(player);
                }
            }

            player.sendMessage(BasicsMessage.COMMAND_VANISH_SET.asString(account));
        }

        session.update();
    }

}
