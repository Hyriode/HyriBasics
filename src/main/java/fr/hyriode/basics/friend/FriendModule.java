package fr.hyriode.basics.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.packet.HyriChannel;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.HyriFriendRequest;
import fr.hyriode.api.player.model.modules.IHyriFriendsModule;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.utils.Symbols;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 08:44
 */
public class FriendModule {

    public FriendModule() {
        HyriAPI.get().getNetworkManager().getEventBus().register(new FriendListener());
        HyriAPI.get().getPubSub().subscribe(HyriChannel.FRIENDS, new FriendReceiver(this));
    }

    public void onRequest(HyriFriendRequest request) {
        final Player player = Bukkit.getPlayer(request.getTarget());

        if (player != null) {
            final IHyriPlayer sender = IHyriPlayer.get(request.getSender());

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_REQUEST_RECEIVED_MESSAGE.asString(player).replace("%player%", sender.getNameWithRank()))
                    .append("\n")
                    .append(BasicsMessage.BUTTON_ACCEPT.asString(player))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BasicsMessage.FRIEND_ACCEPT_HOVER.asString(player))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept " + sender.getName()))
                    .append(" ")
                    .append(BasicsMessage.BUTTON_DENY.asString(player))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(BasicsMessage.FRIEND_DENY_HOVER.asString(player))))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deny " + sender.getName()))));
        }
    }

    public boolean hasRequest(Player player, IHyriFriendsModule friends, IHyriPlayer sender) {
        if (!friends.hasRequest(sender.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NO_REQUEST_MESSAGE.asString(player).replace("%player%", sender.getNameWithRank()))));
            return false;
        }
        return true;
    }

    public boolean areFriends(IHyriFriendsModule friends, Player player, IHyriPlayer target) {
        if (friends.has(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_ALREADY_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
            return true;
        }
        return false;
    }

    public boolean areNotFriends(IHyriFriendsModule friends, Player player, IHyriPlayer target) {
        if (!friends.has(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NOT_ALREADY_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
            return true;
        }
        return false;
    }

    public static BaseComponent[] createMessage(Consumer<ComponentBuilder> append) {
        final ComponentBuilder builder = new ComponentBuilder(Symbols.HYPHENS_LINE).color(ChatColor.LIGHT_PURPLE).strikethrough(true)
                .append("\n").strikethrough(false);

        append.accept(builder);

        builder.append("\n")
                .append(Symbols.HYPHENS_LINE)
                .event((ClickEvent) null)
                .event((HoverEvent) null)
                .color(ChatColor.LIGHT_PURPLE)
                .strikethrough(true);

        return builder.create();
    }

}
