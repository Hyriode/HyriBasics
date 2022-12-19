package fr.hyriode.basics.chat.channel;

import fr.hyriode.api.chat.channel.IHyriChatChannelHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 07/12/2022 at 15:13
 */
public class StaffChannelHandler implements IHyriChatChannelHandler {

    @Override
    public void onMessage(UUID sender, String message, boolean component) {
        final IHyriPlayerSession senderSession = IHyriPlayerSession.get(sender);

        if (senderSession == null) {
            return;
        }

        final Player player = Bukkit.getPlayer(sender);
        final IHyriPlayer senderAccount = IHyriPlayer.get(sender);

        if (!senderAccount.getRank().isStaff()) {
            player.sendMessage(BasicsMessage.CHAT_CHANNEL_PERMISSION_ERROR.asString(senderAccount));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            final IHyriPlayer targetAccount = IHyriPlayer.get(target.getUniqueId());

            if (!targetAccount.getRank().isStaff()) {
                continue;
            }

            target.sendMessage(ChatColor.AQUA + "Staff » " + senderAccount.getNameWithRank() + ChatColor.WHITE + ": " + message);
        }
    }

}
