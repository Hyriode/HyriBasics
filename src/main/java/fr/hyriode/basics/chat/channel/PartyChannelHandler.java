package fr.hyriode.basics.chat.channel;

import fr.hyriode.api.chat.channel.IHyriChatChannelHandler;
import fr.hyriode.api.party.IHyriParty;
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
public class PartyChannelHandler implements IHyriChatChannelHandler {

    @Override
    public void onMessage(UUID sender, String message, boolean component) {
        final IHyriPlayerSession senderSession = IHyriPlayerSession.get(sender);

        if (senderSession == null) {
            return;
        }

        final Player player = Bukkit.getPlayer(sender);
        final IHyriPlayer senderAccount = IHyriPlayer.get(sender);
        final IHyriParty party = IHyriParty.get(senderSession.getParty());

        if (party == null) {
            if (player != null) {
                player.sendMessage(BasicsMessage.CHAT_CHANNEL_PARTY_ERROR.asString(senderAccount));
            }
            return;
        }

        if (!party.isChatEnabled() && !party.getRank(sender).canMute()) {
            if (player != null) {
                player.sendMessage(BasicsMessage.PARTY_CHAT_MUTED_MESSAGE.asString(player));
            }
            return;
        }

        for (UUID member : party.getMembers().keySet()) {
            final Player target = Bukkit.getPlayer(member);

            if (target == null) {
                continue;
            }

            target.sendMessage(ChatColor.DARK_AQUA + "Party » " + senderAccount.getNameWithRank() + ChatColor.WHITE + ": " + message);
        }
    }

}
