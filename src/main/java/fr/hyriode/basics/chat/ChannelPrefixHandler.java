package fr.hyriode.basics.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 15/05/2023 at 20:05
 */
public class ChannelPrefixHandler implements IHyriChatHandler {

    @Override
    public boolean onChat(AsyncPlayerChatEvent event) {
        final UUID playerId = event.getPlayer().getUniqueId();
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final String message = event.getMessage();

        if (message.startsWith("&") && message.length() > 1 && HyriChatChannel.STAFF.hasAccess(account)) {
            event.setCancelled(true);

            HyriAPI.get().getChatChannelManager().sendMessage(HyriChatChannel.STAFF, playerId, message.substring(1), false);
            return false;
        }
        return true;
    }

    @Override
    public String format() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {}

}
