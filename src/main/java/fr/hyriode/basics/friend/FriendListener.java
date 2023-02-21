package fr.hyriode.basics.friend;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.event.PlayerJoinNetworkEvent;
import fr.hyriode.api.player.event.PlayerQuitNetworkEvent;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 29/06/2022 at 17:02
 */
public class FriendListener {

    @HyriEventHandler
    public void onNetworkJoin(PlayerJoinNetworkEvent event) {
        this.checkNotification(event.getPlayer(), BasicsMessage.FRIEND_JOINED_MESSAGE.asLang());
    }

    @HyriEventHandler
    public void onNetworkQuit(PlayerQuitNetworkEvent event) {
        this.checkNotification(event.getPlayer(), BasicsMessage.FRIEND_LEFT_MESSAGE.asLang());
    }

    private void checkNotification(IHyriPlayer friend, HyriLanguageMessage message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final IHyriPlayer target = IHyriPlayer.get(player.getUniqueId());

            if (target.getFriends().has(friend.getUniqueId()) && target.getSettings().isFriendConnectionNotificationEnabled()) {
                player.sendMessage(message.getValue(target).replace("%player%", friend.getName()));
            }
        }
    }

}
