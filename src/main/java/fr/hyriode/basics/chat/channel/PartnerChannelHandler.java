package fr.hyriode.basics.chat.channel;

import fr.hyriode.api.chat.channel.IHyriChatChannelHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 07/12/2022 at 15:13
 */
public class PartnerChannelHandler implements IHyriChatChannelHandler {

    @Override
    public void onMessage(UUID sender, String message, boolean component) {
        final IHyriPlayerSession senderSession = IHyriPlayerSession.get(sender);

        if (senderSession == null) {
            return;
        }

        final Player player = Bukkit.getPlayer(sender);
        final IHyriPlayer senderAccount = IHyriPlayer.get(sender);

        if (!senderAccount.getRank().is(PlayerRank.PARTNER)) {
            player.sendMessage(BasicsMessage.CHAT_CHANNEL_PERMISSION_ERROR.asString(senderAccount));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            final IHyriPlayer targetAccount = IHyriPlayer.get(target.getUniqueId());

            if (!targetAccount.getRank().is(PlayerRank.PARTNER)) {
                continue;
            }

            target.sendMessage(ChatColor.GOLD + "Partner » " + senderAccount.getNameWithRank() + ChatColor.WHITE + ": " + message);
        }
    }

}
