package fr.hyriode.basics.language;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.player.IHyriPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by AstFaster
 * on 23/07/2022 at 09:51
 */
public enum BasicsMessage {

    CHAT_CHANNEL_PARTY_ERROR("chat-channel.party.error"),
    CHAT_CHANNEL_PERMISSION_ERROR("chat-channel.permission.error"),

    COMMAND_PING_MESSAGE("command.ping.message"),

    COMMAND_VANISH_SET("command.vanish.set"),
    COMMAND_VANISH_UNSET("command.vanish.unset"),
    COMMAND_VANISH_GAME("command.vanish.game"),

    COMMAND_DISCORD_MESSAGE("command.discord.message"),
    COMMAND_WEBSITE_MESSAGE("command.website.message"),
    COMMAND_STORE_MESSAGE("command.store.message"),
    COMMAND_LOBBY_MESSAGE("command.lobby.message"),

    COMMAND_MAINTENANCE_ON("command.maintenance.on"),
    COMMAND_MAINTENANCE_OFF("command.maintenance.off"),

    COMMAND_WHITELIST_ALREADY_IN("command.whitelist.already-in"),
    COMMAND_WHITELIST_ADDED("command.whitelist.added"),

    COMMAND_REJOIN_ADDED("command.rejoin.processing"),
    COMMAND_REJOIN_CANCEL("command.rejoin.cancel"),

    COMMAND_PLUGINS_MESSAGE("command.plugins.message"),
    COMMAND_PLUGINS_HOVER_NAME("command.plugins.hover.name"),
    COMMAND_PLUGINS_HOVER_VERSION("command.plugins.hover.version"),
    COMMAND_PLUGINS_HOVER_AUTHORS("command.plugins.hover.authors"),

    COMMAND_CHAT_CHANNEL_NOW_TALKING("command.chat-channel.now-talking"),
    COMMAND_CHAT_CHANNEL_INVALID("command.chat-channel.invalid"),
    COMMAND_CHAT_CHANNEL_ALREADY_IN("command.chat-channel.already-in"),
    COMMAND_CHAT_CHANNEL_CANT_JOIN("command.chat-channel.cant-join"),
    COMMAND_CHAT_CHANNEL_CANT_TALK("command.chat-channel.cant-talk"),

    COMMAND_PRIVATE_MESSAGE_HIMSELF("command.private-message.himself"),
    COMMAND_PRIVATE_MESSAGE_NO_PLAYER_TO_REPLY("command.private-message.no-player-to-reply"),
    COMMAND_PRIVATE_MESSAGE_NOT_ONLINE("command.private-message.not-online"),
    COMMAND_PRIVATE_MESSAGE_RECEIVED("command.private-message.received"),
    COMMAND_PRIVATE_MESSAGE_SENT("command.private-message.sent"),
    COMMAND_PRIVATE_MESSAGE_REPLY("command.private-message.reply"),
    COMMAND_PRIVATE_MESSAGE_DOESNT_ACCEPT("command.private-message.doesnt-accept"),

    COMMAND_TIP_NO_BOOSTER("command.tip.no-booster"),
    COMMAND_TIP_PLAYER("command.tip.player"),
    COMMAND_TIP_BOOSTER_OWNER("command.tip.booster-owner"),

    FRIEND_YOURSELF_MESSAGE("friend.yourself.message"),
    FRIEND_NOT_ONLINE_MESSAGE("friend.not-online.message"),
    FRIEND_ALREADY_MESSAGE("friend.already.message"),
    FRIEND_NOT_ALREADY_MESSAGE("friend.not-already.message"),
    FRIEND_DOESNT_ACCEPT_MESSAGE("friend.doesnt-accept.message"),
    FRIEND_REQUEST_SENT_MESSAGE("friend.request-sent.message"),
    FRIEND_REQUEST_RECEIVED_MESSAGE("friend.request-received.message"),
    FRIEND_REQUEST_ALREADY_MESSAGE("friend.request-already.message"),
    FRIEND_NO_REQUEST_MESSAGE("friend.no-request.message"),
    FRIEND_NO_FRIEND_MESSAGE("friend.no-friend.message"),
    FRIEND_ACCEPT_MESSAGE("friend.accept.message"),
    FRIEND_NO_LONGER_MESSAGE("friend.no-longer.message"),
    FRIEND_DENY_SENDER_MESSAGE("friend.deny-sender.message"),
    FRIEND_DENY_TARGET_MESSAGE("friend.deny-target.message"),
    FRIEND_LIST_PLAYER_MESSAGE("friend.list-player.message"),
    FRIEND_LIST_PLAYER_OFFLINE_MESSAGE("friend.list-player-offline.message"),
    FRIEND_LIMIT_MESSAGE("friend.limit.message"),
    FRIEND_LIMIT_TARGET_MESSAGE("friend.limit-target.message"),
    FRIEND_ACCEPT_HOVER("friend.accept.hover"),
    FRIEND_DENY_HOVER("friend.deny.hover"),
    FRIEND_JOINED_MESSAGE("friend.joined.message"),
    FRIEND_LEFT_MESSAGE("friend.left.message"),

    PARTY_YOURSELF_MESSAGE("party.yourself.message"),
    PARTY_INVITATION_SENT_MESSAGE("party.invitation-sent.message"),
    PARTY_INVITATION_RECEIVED_MESSAGE("party.invitation-received.message"),
    PARTY_ALREADY_INVITED_MESSAGE("party.already-invited.message"),
    PARTY_ALREADY_IN_YOUR_MESSAGE("party.already-in-your.message"),
    PARTY_ALREADY_IN_ONE_MESSAGE("party.already-in-one.message"),
    PARTY_DOESNT_ACCEPT_MESSAGE("party.doesnt-accept.message"),
    PARTY_NO_PERMISSION_MESSAGE("party.no-permission.message"),
    PARTY_DOESNT_HAVE_SENDER_MESSAGE("party.doesnt-have-sender.message"),
    PARTY_DOESNT_HAVE_TARGET_MESSAGE("party.doesnt-have-target.message"),
    PARTY_NO_INVITATION_MESSAGE("party.no-invitation.message"),
    PARTY_FULL_YOUR_MESSAGE("party.full-your.message"),
    PARTY_FULL_IT_MESSAGE("party.full-it.message"),
    PARTY_JOIN_PLAYER_MESSAGE("party.join-player.message"),
    PARTY_JOIN_OTHERS_MESSAGE("party.join-others.message"),
    PARTY_DENY_TARGET_MESSAGE("party.deny-target.message"),
    PARTY_DENY_SENDER_MESSAGE("party.deny-sender.message"),
    PARTY_LEFT_PLAYER_MESSAGE("party.left-player.message"),
    PARTY_LEFT_OTHERS_MESSAGE("party.left-others.message"),
    PARTY_NOT_IN_MESSAGE("party.not-in.message"),
    PARTY_CANT_KICK_MESSAGE("party.cant-kick.message"),
    PARTY_KICK_OTHERS_MESSAGE("party.kick-others.message"),
    PARTY_KICK_TARGET_MESSAGE("party.kick-target.message"),
    PARTY_LEAD_TRANSFER_MESSAGE("party.lead-transfer.message"),
    PARTY_CANT_LEAVE_LEADER_MESSAGE("party.cant-leave-leader.message"),
    PARTY_DISBAND_PLAYER_MESSAGE("party.disband-player.message"),
    PARTY_DISBAND_OTHERS_MESSAGE("party.disband-others.message"),
    PARTY_CANT_PROMOTE_MESSAGE("party.cant-promote.message"),
    PARTY_PROMOTE_MESSAGE("party.promote.message"),
    PARTY_CANT_DEMOTE_MESSAGE("party.cant-demote.message"),
    PARTY_DEMOTE_MESSAGE("party.demote.message"),
    PARTY_CHAT_MUTED_MESSAGE("party.chat-muted.message"),
    PARTY_CHAT_ALREADY_DISABLED_MESSAGE("party.chat-already-disabled.message"),
    PARTY_CHAT_ALREADY_ENABLED_MESSAGE("party.chat-already-enabled.message"),
    PARTY_CHAT_ENABLED_MESSAGE("party.chat-enabled.message"),
    PARTY_CHAT_DISABLED_MESSAGE("party.chat-disabled.message"),
    PARTY_RANK_FEATURE_MESSAGE("party.rank-feature.message"),
    PARTY_ACCESS_PRIVATE_MESSAGE("party.access-private.message"),
    PARTY_ACCESS_PUBLIC_MESSAGE("party.access-public.message"),
    PARTY_TP_MESSAGE("party.tp.message"),
    PARTY_LIMIT_MESSAGE("party.limit.message"),
    PARTY_LIMIT_TARGET_MESSAGE("party.limit-target.message"),
    PARTY_INFORMATION_MESSAGE("party.information.message"),
    PARTY_INFORMATION_PLAYER_ONLINE_MESSAGE("party.information-player-online.message"),
    PARTY_INFORMATION_PLAYER_OFFLINE_MESSAGE("party.information-player-offline.message"),
    PARTY_ACCEPT_HOVER("party.accept.hover"),
    PARTY_DENY_HOVER("party.deny.hover"),

    NICKNAME_PREFIX("nickname.prefix"),
    NICKNAME_PLAYER_EXISTS_MESSAGE("nickname.player-exists.message", NICKNAME_PREFIX),
    NICKNAME_NOT_NICK_MESSAGE("nickname.not-nick.message", NICKNAME_PREFIX),
    NICKNAME_ADD_NICK_MESSAGE("nickname.add-nick.message", NICKNAME_PREFIX),
    NICKNAME_REMOVE_NICK_MESSAGE("nickname.remove-nick.message", NICKNAME_PREFIX),
    NICKNAME_GAME_MESSAGE("nickname.game.message", NICKNAME_PREFIX),

    NICKNAME_GUI_APPLY_NAME("gui.nickname.apply.name"),
    NICKNAME_GUI_APPLY_LORE("gui.nickname.apply.lore"),
    NICKNAME_GUI_NICK_NAME("gui.nickname.nick.name"),
    NICKNAME_GUI_NICK_LORE("gui.nickname.nick.lore"),
    NICKNAME_GUI_SKIN_NAME("gui.nickname.skin.name"),
    NICKNAME_GUI_SKIN_LORE("gui.nickname.skin.lore"),
    NICKNAME_GUI_RANK_NAME("gui.nickname.rank.name"),
    NICKNAME_GUI_RANK_LORE("gui.nickname.rank.lore"),
    NICKNAME_GUI_RANDOM_NAME("gui.nickname.random.name"),
    NICKNAME_GUI_RANDOM_SKIN("gui.nickname.random.skin"),
    NICKNAME_GUI_RANDOM_RANK("gui.nickname.random.rank"),

    BUTTON_ACCEPT("button.accept"),
    BUTTON_DENY("button.deny"),

    TAB_INFORMATION("tab.information"),
    TAB_WEBSITE_FORUM("tab.website-forum"),
    TAB_STORE("tab.store"),
    TAB_DISCORD("tab.discord"),

    ANNOUNCEMENT_MESSAGE("announcement.message"),
    BOOSTER_MESSAGE("booster.message"),
    LEVELING_MESSAGE("leveling.message"),

    PLAYER_RANK("player.rank")

    ;

    private HyriLanguageMessage languageMessage;

    private final String key;
    private final BiFunction<IHyriPlayer, String, String> formatter;

    BasicsMessage(String key, BiFunction<IHyriPlayer, String, String> formatter) {
        this.key = key;
        this.formatter = formatter;
    }

    BasicsMessage(String key, BasicsMessage prefix) {
        this.key = key;
        this.formatter = (target, input) -> prefix.asString(target) + input;
    }

    BasicsMessage(String key) {
        this(key, (target, input) -> input);
    }

    public HyriLanguageMessage asLang() {
        return this.languageMessage == null ? this.languageMessage = HyriLanguageMessage.get(this.key) : this.languageMessage;
    }

    public String asString(IHyriPlayer account) {
        return this.formatter.apply(account, this.asLang().getValue(account));
    }

    public String asString(Player player) {
        return this.asString(IHyriPlayer.get(player.getUniqueId()));
    }

    public String asString(CommandSender sender) {
        return this.asLang().getValue(sender);
    }

    public void sendTo(Player player) {
        player.sendMessage(this.asString(player));
    }

    public List<String> asList(IHyriPlayer account) {
        return new ArrayList<>(Arrays.asList(this.asString(account).split("\n")));
    }

    public List<String> asList(Player player) {
        return this.asList(IHyriPlayer.get(player.getUniqueId()));
    }

}
