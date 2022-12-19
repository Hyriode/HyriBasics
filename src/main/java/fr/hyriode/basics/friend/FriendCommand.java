package fr.hyriode.basics.friend;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.friend.IHyriFriend;
import fr.hyriode.api.friend.IHyriFriendHandler;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.utils.Pagination;
import fr.hyriode.hyrame.utils.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static fr.hyriode.basics.friend.FriendModule.createMessage;

/**
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class FriendCommand extends HyriCommand<HyriBasics> {

    private static final Function<Player, BaseComponent[]> HELP = player -> new HelpCommandCreator("f", "friend", player)
            .withMainColor(ChatColor.DARK_PURPLE)
            .withSecondaryColor(ChatColor.LIGHT_PURPLE)
            .addArgumentsLine("add", "add <player>")
            .addArgumentsLine("remove", "remove <player>")
            .addArgumentsLine("list", "list")
            .create();

    public FriendCommand(HyriBasics plugin) {
        super(plugin, new HyriCommandInfo("friend")
                .withAliases("f", "ami", "friends", "amis")
                .withDescription("The command used to manage friends")
                .withType(HyriCommandType.PLAYER)
                .withUsage(sender -> HELP.apply((Player) sender), false));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(playerId);
        final FriendModule friendModule = HyriBasics.get().getFriendModule();
        final IHyriFriendHandler friendHandler = HyriAPI.get().getFriendManager().createHandler(playerId);

        this.handleArgument(ctx, "accept %player%", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!friendModule.hasRequest(player, sender)) {
                return;
            }

            final int friendsLimit = FriendLimit.getMaxFriends(account.getRank().getPlayerType());

            if (friendHandler.getFriends().size() >= friendsLimit) { // Check if player has reached his limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_MESSAGE.asString(player).replace("%limit%", String.valueOf(friendsLimit)))));
                return;
            }

            if (sender.getFriendHandler().getFriends().size() >= FriendLimit.getMaxFriends(sender.getRank().getPlayerType())) { // Check if requester has reached friends limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_TARGET_MESSAGE.asString(player).replace("%player%", sender.getNameWithRank()))));
                return;
            }

            HyriAPI.get().getFriendManager().removeRequest(playerId, sender.getUniqueId()); // Remove request
            friendHandler.addFriend(sender.getUniqueId()); // Set players as friends

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(BasicsMessage.FRIEND_ACCEPT_MESSAGE.asString(sender).replace("%player%", account.getNameWithRank())))); // Send message to the request sender
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_ACCEPT_MESSAGE.asString(account).replace("%player%", sender.getNameWithRank())))); // Send the message to the request receiver
        });

        this.handleArgument(ctx, "deny %player%", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!friendModule.hasRequest(player, sender)) {
                return;
            }

            HyriAPI.get().getFriendManager().removeRequest(playerId, sender.getUniqueId()); // Remove request

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(BasicsMessage.FRIEND_DENY_SENDER_MESSAGE.asString(sender).replace("%player%", account.getNameWithRank())))); // Send the message to the request sender
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DENY_TARGET_MESSAGE.asString(account).replace("%player%", sender.getNameWithRank())))); // Send the message to the request receiver
        });

        this.handleArgument(ctx, "remove %player%", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (friendModule.areNotFriends(friendHandler, player, target)) { // Check whether they are not friends
                return;
            }

            friendHandler.removeFriend(target.getUniqueId()); // Set players as not friends

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NO_LONGER_MESSAGE.asString(account).replace("%player%", target.getNameWithRank())))); // Send message to the player
        });

        this.handleArgument(ctx, "list %integer%", output -> this.listFriends(output.get(Integer.class), player, account, friendHandler));
        this.handleArgument(ctx, "list", output -> this.listFriends(0, player, account, friendHandler));
        this.handleArgument(ctx, "help", output -> player.spigot().sendMessage(HELP.apply(player)));
        this.handleArgument(ctx, "%player_online%", this.addFriend(player, account, friendHandler));
        this.handleArgument(ctx, "add %player_online%", this.addFriend(player, account, friendHandler));
    }

    private Consumer<HyriCommandOutput> addFriend(Player player, IHyriPlayer account, IHyriFriendHandler friendHandler) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (target.getUniqueId().equals(playerId)) { // Check if the player is trying to add himself as a friend
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_YOURSELF_MESSAGE.asString(account))));
                return;
            }

            if (HyriBasics.get().getFriendModule().areFriends(friendHandler, player, target)) { // Check if the players are already friends
                return;
            }

            if (HyriAPI.get().getFriendManager().hasRequest(target.getUniqueId(), playerId)) { // Check if the player already sent a request to the target
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_REQUEST_ALREADY_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
                return;
            }

            final IHyriPlayerSession targetSession = IHyriPlayerSession.get(target.getUniqueId());

            if (targetSession == null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NOT_ONLINE_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            final int friendsLimit = FriendLimit.getMaxFriends(account.getRank().getPlayerType());

            if (friendHandler.getFriends().size() >= friendsLimit) { // Check if player has reached his friends limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_MESSAGE.asString(account).replace("%limit%", String.valueOf(friendsLimit)))));
                return;
            }

            if (targetSession.hasNickname() && !account.getRank().isStaff()) { // Check if the target has a nickname
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", targetSession.getNameWithRank()))));
                return;
            }

            if (!target.getSettings().isFriendRequestsEnabled() && !account.getRank().isStaff()) { // Check if the target accepts friend requests
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            HyriAPI.get().getFriendManager().sendRequest(player.getUniqueId(), target.getUniqueId()); // Finally, send request

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_REQUEST_SENT_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
        };
    }

    private void listFriends(int page, Player player, IHyriPlayer account, IHyriFriendHandler friendHandler) {
        final List<IHyriFriend> friends = friendHandler.getFriends();

        if (friends.size() == 0) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NO_FRIEND_MESSAGE.asString(account))));
            return;
        }

        final Pagination<ListedFriend> showingFriends = new Pagination<>(10);

        for (IHyriFriend friend : friends) {
            final UUID playerId = friend.getUniqueId();
            final IHyriPlayer friendAccount = IHyriPlayer.get(playerId);

            if (friendAccount == null) {
                continue;
            }

            showingFriends.add(new ListedFriend(friendAccount, IHyriPlayerSession.get(playerId)));
        }

        showingFriends.sort(Comparator.comparing(friend -> friend.getSession() != null));

        player.spigot().sendMessage(createMessage(builder -> {
            for (ListedFriend friend : showingFriends.getPageContent(page)) {
                final IHyriPlayer friendAccount = friend.getAccount();
                final IHyriPlayerSession friendSession = friend.getSession();

                if (friendSession != null) {
                    final String server = friendSession.getServer();

                    builder.append(BasicsMessage.FRIEND_LIST_PLAYER_MESSAGE.asString(account)
                            .replace("%player%", friendAccount.getNameWithRank())
                            .replace("%server%", server != null ? server : "?"));
                } else {
                    builder.append(BasicsMessage.FRIEND_LIST_PLAYER_OFFLINE_MESSAGE.asString(account)
                            .replace("%player%", friendAccount.getNameWithRank()));
                }

                if (showingFriends.indexOf(friend) != showingFriends.size() - 1) {
                    builder.append("\n");
                }
            }
        }));
    }

    private static class ListedFriend {

        private final IHyriPlayer account;
        private final IHyriPlayerSession session;

        public ListedFriend(IHyriPlayer account, IHyriPlayerSession session) {
            this.account = account;
            this.session = session;
        }

        public IHyriPlayer getAccount() {
            return this.account;
        }

        public IHyriPlayerSession getSession() {
            return this.session;
        }

    }

}
