package fr.hyriode.basics.message;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.chat.PrivateMessageEvent;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.SettingsLevel;
import fr.hyriode.api.sound.HyriSound;
import fr.hyriode.api.sound.HyriSoundPacket;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 21/04/2022 at 15:35
 */
public class PrivateMessageModule {

    public void replyToMessage(Player player, String message) {
        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());
        final UUID messageTarget = session.getPrivateMessageTarget();

        if (messageTarget == null) {
            player.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_NO_PLAYER_TO_REPLY.asString(player));
            return;
        }

        this.sendPrivateMessage(player, IHyriPlayer.get(messageTarget), message);
    }

    public void sendPrivateMessage(Player sender, IHyriPlayer targetAccount, String message) {
        final UUID senderId = sender.getUniqueId();
        final UUID targetId = targetAccount.getUniqueId();
        final IHyriPlayer senderAccount = IHyriPlayer.get(senderId);

        if (targetId.equals(senderId)) {
            sender.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_HIMSELF.asString(senderAccount));
            return;
        }

        if (!HyriAPI.get().getPlayerManager().isOnline(targetId)) {
            sender.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_NOT_ONLINE.asString(senderAccount).replace("%player%", targetAccount.getNameWithRank()));
            return;
        }

        final PrivateMessageEvent event = new PrivateMessageEvent(senderId, targetId, message);

        HyriAPI.get().getEventBus().publish(event);

        if (event.isCancelled()) {
            return;
        }

        final boolean areFriends = targetAccount.getFriends().has(senderId);
        final boolean isStaff = senderAccount.getRank().isStaff();
        final Predicate<SettingsLevel> levelValidation = level -> level == SettingsLevel.ALL || (level == SettingsLevel.FRIENDS && areFriends) || isStaff;
        final IHyriPlayerSession targetSession = IHyriPlayerSession.get(targetId);

        // The player has a nickname, no one (except staff members) should send a message to him
        if (targetSession.getNickname().has() && !isStaff) {
            sender.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_DOESNT_ACCEPT.asString(sender).replace("%player%", targetAccount.getNameWithRank()));
            return;
        }

        if (levelValidation.test(targetAccount.getSettings().getPrivateMessagesLevel())) {
            if (levelValidation.test(targetAccount.getSettings().getPrivateMessagesSoundLevel())) {
                HyriSoundPacket.send(targetId, HyriSound.ORB_PICKUP, 1.0F, 1.5F);
            }

            targetSession.setPrivateMessageTarget(senderId);
            targetSession.update();

            sender.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_SENT.asString(sender).replace("%player%", targetAccount.getNameWithRank()).replace("%message%", message));

            PlayerUtil.sendComponent(targetId, this.createReceivedMessage(targetAccount, senderAccount, message));
        } else {
            sender.sendMessage(BasicsMessage.COMMAND_PRIVATE_MESSAGE_DOESNT_ACCEPT.asString(sender).replace("%player%", targetAccount.getNameWithRank()));
        }
    }

    private BaseComponent[] createReceivedMessage(IHyriPlayer target, IHyriPlayer sender, String message) {
        final String reply = BasicsMessage.COMMAND_PRIVATE_MESSAGE_REPLY.asString(target).replace("%player%", sender.getNameWithRank());
        final ComponentBuilder builder = new ComponentBuilder(BasicsMessage.COMMAND_PRIVATE_MESSAGE_RECEIVED.asString(target).replace("%player%", sender.getNameWithRank()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(reply))).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/r "))
                .append(message).color(ChatColor.AQUA).event((HoverEvent) null).event((ClickEvent) null);

        return builder.create();
    }

}
