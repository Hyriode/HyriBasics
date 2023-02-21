package fr.hyriode.basics.nickname;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.IHyriNickname;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
import fr.hyriode.hyrame.language.HyrameMessage;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 21/04/2022 at 21:14
 */
public class NicknameCommand extends HyriCommand<HyriBasics> {

    public NicknameCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("nick")
                .withAliases("disguise")
                .withDescription("The command used to edit profile name, skin, etc.")
                .withType(HyriCommandType.PLAYER)
                .withPermission(account -> account.getHyriPlus().has()) // Handle staff, Hyri+ and Partners
                .withUsage("/nick [custom|reset]")
                .asynchronous());
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();

        if (IHyrame.get().getGame() != null) {
            player.sendMessage(BasicsMessage.NICKNAME_GAME_MESSAGE.asString(player));
            return;
        }

        final NicknameModule nicknameModule = HyriBasics.get().getNicknameModule();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());
        final IHyriNickname currentNickname = session.getNickname();

        if (ctx.getArgs().length == 0) {
            nicknameModule.processNickname(player);
            return;
        }

        this.handleArgument(ctx, "custom", output -> {
            if (account.getRank().is(PlayerRank.PARTNER)) {
                ThreadUtil.backOnMainThread(HyriBasics.get(), () -> { // Get back on server thread to open the GUI
                    if (currentNickname != null) {
                        new NicknameGUI(player, nicknameModule, currentNickname.getName(), currentNickname.getSkinOwner(), currentNickname.getSkin(), currentNickname.getRank()).open();
                    } else {
                        new NicknameGUI(player, nicknameModule, null, nicknameModule.getLoader().getRandomSkin(), PlayerRank.PLAYER).open();
                    }
                });
            } else {
                player.sendMessage(HyrameMessage.PERMISSION_ERROR.asString(account));
            }
        });

        this.handleArgument(ctx, "reset", output -> {
            if (currentNickname != null) {
                nicknameModule.resetNickname(player);

                player.sendMessage(BasicsMessage.NICKNAME_REMOVE_NICK_MESSAGE.asString(account));
            } else {
                player.sendMessage(BasicsMessage.NICKNAME_NOT_NICK_MESSAGE.asString(account));
            }
        });
    }

}
