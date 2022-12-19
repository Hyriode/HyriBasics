package fr.hyriode.basics.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.friend.HyriFriendRequest;
import fr.hyriode.api.friend.IHyriFriendHandler;
import fr.hyriode.api.friend.IHyriFriendManager;
import fr.hyriode.api.player.IHyriPlayer;
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
        HyriAPI.get().getPubSub().subscribe(IHyriFriendManager.REDIS_CHANNEL, new FriendReceiver(this));
    }

    public void onRequest(HyriFriendRequest request) {
        final Player player = Bukkit.getPlayer(request.getReceiver());

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

    public boolean hasRequest(Player player, IHyriPlayer sender) {
        if (!HyriAPI.get().getFriendManager().hasRequest(player.getUniqueId(), sender.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NO_REQUEST_MESSAGE.asString(player).replace("%player%", sender.getNameWithRank()))));
            return false;
        }
        return true;
    }

    public boolean areFriends(IHyriFriendHandler friendHandler, Player player, IHyriPlayer target) {
        if (friendHandler.areFriends(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_ALREADY_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
            return true;
        }
        return false;
    }

    public boolean areNotFriends(IHyriFriendHandler friendHandler, Player player, IHyriPlayer target) {
        if (!friendHandler.areFriends(target.getUniqueId())) {
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
