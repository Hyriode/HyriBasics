package fr.hyriode.basics.chat.channel;

import fr.hyriode.api.chat.channel.IHyriChatChannelHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.SettingsLevel;
import fr.hyriode.api.rank.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 07/12/2022 at 15:13
 */
public class GlobalChannelHandler implements IHyriChatChannelHandler {

    @Override
    public void onMessage(UUID sender, String message, boolean component) {
        final IHyriPlayerSession senderSession = IHyriPlayerSession.get(sender);
        final IHyriPlayer senderAccount = IHyriPlayer.get(sender);

        if (senderSession == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            final UUID playerId = player.getUniqueId();
            final SettingsLevel level = playerId.equals(sender) ? senderAccount.getSettings().getGlobalChatLevel() : IHyriPlayer.get(playerId).getSettings().getGlobalChatLevel();

            if (level == SettingsLevel.NONE || (level == SettingsLevel.FRIENDS && !senderAccount.getFriends().has(playerId))) {
                continue;
            }

            if (senderSession.getNickname().has()) {
                player.sendMessage(senderSession.getNameWithRank() + (senderSession.getNickname().getRank() == PlayerRank.PLAYER ? ChatColor.GRAY : ChatColor.WHITE) + ": " + message);
            } else {
                player.sendMessage(senderSession.getNameWithRank() + (senderAccount.getRank().isDefault() ? ChatColor.GRAY : ChatColor.WHITE) + ": " + message);
            }
        }
    }

}
