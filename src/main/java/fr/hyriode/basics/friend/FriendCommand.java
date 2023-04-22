package fr.hyriode.basics.friend;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.IHyriFriend;
import fr.hyriode.api.player.model.modules.IHyriFriendsModule;
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
        super(plugin, new CommandInfo("friend")
                .withAliases("f", "ami", "friends", "amis")
                .withDescription("The command used to manage friends")
                .withUsage(new CommandUsage(HELP, false)));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final FriendModule friendModule = HyriBasics.get().getFriendModule();
        final IHyriFriendsModule friends = account.getFriends();

        ctx.registerArgument("accept %player%", "/f accept <player>", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!friendModule.hasRequest(player, friends, sender)) {
                return;
            }

            final int friendsLimit = FriendLimit.getMaxFriends(account.getRank().getPlayerType());

            if (friends.getAll().size() >= friendsLimit) { // Check if player has reached his limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_MESSAGE.asString(player).replace("%limit%", String.valueOf(friendsLimit)))));
                return;
            }

            if (sender.getFriends().getAll().size() >= FriendLimit.getMaxFriends(sender.getRank().getPlayerType())) { // Check if requester has reached friends limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_TARGET_MESSAGE.asString(player).replace("%player%", sender.getNameWithRank()))));
                return;
            }

            // Remove request
            friends.removeRequest(sender.getUniqueId());

            // Set players as friends
            friends.add(sender.getUniqueId());
            sender.getFriends().add(playerId);

            account.update();
            sender.update();

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(BasicsMessage.FRIEND_ACCEPT_MESSAGE.asString(sender).replace("%player%", account.getNameWithRank())))); // Send message to the request sender
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_ACCEPT_MESSAGE.asString(account).replace("%player%", sender.getNameWithRank())))); // Send the message to the request receiver
        });

        ctx.registerArgument("deny %player%", "/f deny <player>", output -> {
            final IHyriPlayer sender = output.get(IHyriPlayer.class);

            if (!friendModule.hasRequest(player, friends, sender)) {
                return;
            }

            friends.removeRequest(sender.getUniqueId()); // Remove request

            PlayerUtil.sendComponent(sender.getUniqueId(), createMessage(builder -> builder.append(BasicsMessage.FRIEND_DENY_SENDER_MESSAGE.asString(sender).replace("%player%", account.getNameWithRank())))); // Send the message to the request sender
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DENY_TARGET_MESSAGE.asString(account).replace("%player%", sender.getNameWithRank())))); // Send the message to the request receiver
        });

        ctx.registerArgument("remove %player%", "/f remove <player>", output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (friendModule.areNotFriends(friends, player, target)) { // Check whether they are not friends
                return;
            }

            // Set players as not friends
            friends.remove(target.getUniqueId());
            target.getFriends().remove(playerId);

            account.update();
            target.update();

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NO_LONGER_MESSAGE.asString(account).replace("%player%", target.getNameWithRank())))); // Send message to the player
        });

        ctx.registerArgument("list %integer%", "/f list <page>", output -> {
             int page = output.get(Integer.class);

            if (page < 1) {
                page = 1;
            }

            this.listFriends(page - 1, player, account, friends);
        });
        ctx.registerArgument("list", "/f list", output -> this.listFriends(0, player, account, friends));
        ctx.registerArgument("", output -> player.spigot().sendMessage(HELP.apply(player)));
        ctx.registerArgument("help", "/f help", output -> player.spigot().sendMessage(HELP.apply(player)));
        ctx.registerArgument("%player_online%", "/f <player>", this.addFriend(player, account, friends));
        ctx.registerArgument("add %player_online%", "/f add <player>", this.addFriend(player, account, friends));

        super.handle(ctx);
    }

    private Consumer<CommandOutput> addFriend(Player player, IHyriPlayer account, IHyriFriendsModule friends) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            if (target.getUniqueId().equals(playerId)) { // Check if the player is trying to add himself as a friend
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_YOURSELF_MESSAGE.asString(account))));
                return;
            }

            if (HyriBasics.get().getFriendModule().areFriends(friends, player, target)) { // Check if the players are already friends
                return;
            }

            if (target.getFriends().hasRequest(playerId)) { // Check if the player already sent a request to the target
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_REQUEST_ALREADY_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
                return;
            }

            final IHyriPlayerSession targetSession = IHyriPlayerSession.get(target.getUniqueId());

            if (targetSession == null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_NOT_ONLINE_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            final int friendsLimit = FriendLimit.getMaxFriends(account.getRank().getPlayerType());

            if (friends.getAll().size() >= friendsLimit) { // Check if player has reached his friends limit
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_LIMIT_MESSAGE.asString(account).replace("%limit%", String.valueOf(friendsLimit)))));
                return;
            }

            if (targetSession.getNickname().has() && !account.getRank().isStaff()) { // Check if the target has a nickname
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", targetSession.getNameWithRank()))));
                return;
            }

            if (!target.getSettings().isFriendRequestsEnabled() && !account.getRank().isStaff()) { // Check if the target accepts friend requests
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            account.getFriends().sendRequest(target.getUniqueId()); // Finally, send request

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.FRIEND_REQUEST_SENT_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
        };
    }

    private void listFriends(int page, Player player, IHyriPlayer account, IHyriFriendsModule friendsModule) {
        final List<IHyriFriend> friends = friendsModule.getAll();

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

        showingFriends.sort(Comparator.comparing(friend -> friend.getSession() == null));

        final List<ListedFriend> pageContent = showingFriends.getPageContent(page);

        if (pageContent.size() == 0) {
            this.listFriends(page - 1, player, account, friendsModule);
            return;
        }

        player.spigot().sendMessage(createMessage(builder -> {
            for (ListedFriend friend : pageContent) {
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

                if (pageContent.indexOf(friend) != pageContent.size() - 1) {
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
