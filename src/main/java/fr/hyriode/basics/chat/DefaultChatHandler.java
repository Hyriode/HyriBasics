package fr.hyriode.basics.chat;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.channel.HyriChatChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriPlayerSettings;
import fr.hyriode.hyrame.chat.IHyriChatHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 07/12/2022 at 15:02
 */
public class DefaultChatHandler implements IHyriChatHandler {

    private boolean cancelled;

    @Override
    public boolean onChat(AsyncPlayerChatEvent event) {
        if (this.cancelled) {
            return true;
        }

        event.setCancelled(true);

        final UUID playerId = event.getPlayer().getUniqueId();
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final IHyriPlayerSettings settings = account.getSettings();

        if (!settings.getChatChannel().hasAccess(account)) {
            settings.setChatChannel(HyriChatChannel.GLOBAL);
            account.update();
        }

        final String message = event.getMessage();

        if (message.startsWith("&") && message.length() > 1 && HyriChatChannel.STAFF.hasAccess(account)) {
            HyriAPI.get().getChatChannelManager().sendMessage(HyriChatChannel.STAFF, playerId, message.substring(1), false);
            return true;
        }

        HyriAPI.get().getChatChannelManager().sendMessage(settings.getChatChannel(), playerId, event.getMessage(), false);
        return true;
    }

    @Override
    public String format() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
