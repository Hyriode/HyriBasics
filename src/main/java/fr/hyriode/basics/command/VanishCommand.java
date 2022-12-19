package fr.hyriode.basics.command;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.rank.type.HyriPlayerRankType;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.utils.PlayerUtil;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 14:35
 */
public class VanishCommand extends HyriCommand<HyriBasics> {

    public VanishCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("vanish")
                .withDescription("The command used to vanish yourself from other players")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/vanish")
                .withPermission(player -> player.getRank().is(HyriPlayerRankType.PARTNER)));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final HyriGame<?> currentGame = this.plugin.getHyrame().getGameManager().getCurrentGame();

        if (currentGame != null) {
            player.sendMessage(BasicsMessage.COMMAND_VANISH_GAME.asString(account));
            return;
        }

        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());

        if (session.isVanished()) {
            session.setVanished(false);
            PlayerUtil.showPlayer(player);

            player.sendMessage(BasicsMessage.COMMAND_VANISH_UNSET.asString(account));
        } else {
            session.setVanished(true);
            PlayerUtil.hidePlayer(player, true);

            player.sendMessage(BasicsMessage.COMMAND_VANISH_SET.asString(account));
        }

        session.update();
    }

}
