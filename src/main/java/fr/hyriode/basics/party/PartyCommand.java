package fr.hyriode.basics.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.api.party.HyriPartyRank;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.party.IHyriPartyManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.SettingsLevel;
import fr.hyriode.api.rank.PlayerRank;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.*;
import fr.hyriode.hyrame.utils.PlayerUtil;
import fr.hyriode.hyrame.utils.TimeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.hyriode.basics.party.PartyModule.createMessage;

/**
 * Created by AstFaster
 * on 21/04/2022 at 10:38
 */
public class PartyCommand extends HyriCommand<HyriBasics> {

    private static final Function<Player, BaseComponent[]> HELP = player -> new HelpCommandCreator("p", "party", player)
            .addArgumentsLine("invite", "invite <player>")
            .addArgumentsLine("kick", "kick <player>")
            .addArgumentsLine("accept", "accept <player>")
            .addArgumentsLine("deny", "deny <player>")
            .addArgumentsLine("promote", "promote <player>")
            .addArgumentsLine("demote", "demote <player>")
            .addArgumentsLine("lead", "lead <player>")
            .addArgumentsLine("disband", "disband")
            .addArgumentsLine("tp", "tp <player>")
            .addArgumentsLine("info", "info")
            .addArgumentsLine("chat", "chat <message>")
            .addArgumentsLine("mute", "mute <on|off>")
            .addArgumentsLine("stream", "stream")
            .create();

    private final IHyriPartyManager partyManager;

    public PartyCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("party")
                .withAliases("p", "groupe", "group", "partie")
                .withDescription("The command used to create a party and interact with it")
                .withUsage(new CommandUsage(HELP, false)));
        this.partyManager = HyriAPI.get().getPartyManager();
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final UUID playerId = player.getUniqueId();
        final IHyriPlayer account = IHyriPlayer.get(playerId);
        final IHyriParty party = this.partyManager.getPlayerParty(playerId);

        ctx.registerArgument("leave", "/p leave", this.partyOutput(ctx, party, output -> {
            if (party.isLeader(playerId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CANT_LEAVE_LEADER_MESSAGE.asString(account))));
                return;
            }

            party.removeMember(playerId, IHyriParty.RemoveReason.MANUAL);
        }));

        ctx.registerArgument("disband", "/p disband", this.partyOutput(ctx, party, output -> {
            if (!party.getRank(playerId).canDisband()) {
                this.dontHavePermission(player);
                return;
            }

            party.disband(IHyriParty.DisbandReason.NORMAL);
        }));

        ctx.registerArgument("kick %player%", "/p kick <player>", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (!this.isInParty(player, party, target)) {
                return;
            }

            final HyriPartyRank playerRank = party.getRank(playerId);

            if (!playerRank.canKick()) {
                this.dontHavePermission(player);
                return;
            }

            if (party.getRank(targetId).isSuperior(playerRank) && playerRank != HyriPartyRank.LEADER) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CANT_KICK_MESSAGE.asString(account))));
                return;
            }

            party.kickMember(targetId, playerId);
        }));

        ctx.registerArgument("lead %player_online%", "/p lead <player>", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (!this.isInParty(player, party, target)) {
                return;
            }

            if (!party.isLeader(playerId)) {
                this.dontHavePermission(player);
                return;
            }

            party.setLeader(targetId);
        }));

        ctx.registerArgument("promote %player%",  "/p promote <player>", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (this.canEditRank(player, party, target)) {
                if (party.promoteMember(targetId) != null) {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CANT_PROMOTE_MESSAGE.asString(account).replace("%target%", target.getNameWithRank()))));
                }
            }
        }));

        ctx.registerArgument("demote %player%", "/p demote <player>", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            if (this.canEditRank(player, party, target)) {
                if (party.demoteMember(targetId) != null) {
                    player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CANT_DEMOTE_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                }
            }
        }));

        ctx.registerArgument("chat %sentence%", "/p chat <message>", this.partyOutput(ctx, party, output -> party.sendMessage(playerId, output.get(String.class))));

        ctx.registerArgument("mute %input%", "/p mute [on|off]", this.partyOutput(ctx, party, output -> {
            final HyriPartyRank playerRank = party.getRank(playerId);

            if (!playerRank.canMute()) {
                this.dontHavePermission(player);
                return;
            }

            final String mutedInput = output.get(String.class);

            if (!mutedInput.equalsIgnoreCase("on") && !mutedInput.equalsIgnoreCase("off")) {
                player.sendMessage(ChatColor.RED + "/party chat <on|off>");
                return;
            }

            final boolean muted = mutedInput.equalsIgnoreCase("on");

            if (muted == party.isChatEnabled() && muted) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CHAT_ALREADY_ENABLED_MESSAGE.asString(account))));
                return;
            }

            if (muted == party.isChatEnabled() && !muted) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_CHAT_ALREADY_DISABLED_MESSAGE.asString(account))));
                return;
            }

            party.setChatEnabled(!muted);
        }));

        ctx.registerArgument("stream", "/p stream", this.partyOutput(ctx, party, output -> {
            if (!party.isLeader(playerId)) {
                this.dontHavePermission(player);
                return;
            }

            if (!account.getRank().isSuperior(PlayerRank.PARTNER)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_RANK_FEATURE_MESSAGE.asString(account).replace("%rank%", PlayerRank.PARTNER.getDefaultPrefix()))));
                return;
            }

            party.setPrivate(!party.isPrivate());
        }));

        ctx.registerArgument("tp %player_online%", "/p tp <player>", this.partyOutput(ctx, party, output -> {
            final IHyriPlayer target = output.get(IHyriPlayer.class);

            party.teleport(playerId, target.getUniqueId());
            player.sendMessage(BasicsMessage.PARTY_TP_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()));
        }));

        ctx.registerArgument("list", "/p list", this.partyOutput(ctx, party, this.listParty(player, account, party)));
        ctx.registerArgument("info", "/p info", this.partyOutput(ctx, party, this.listParty(player, account, party)));
        ctx.registerArgument("accept %player%", "/p accept <player>", this.joinParty(player, account));
        ctx.registerArgument("join %player%", "/p join <player>", this.joinParty(player, account));
        ctx.registerArgument("deny %player%", "/p deny <player>", output -> {
            final IHyriPlayer requester = output.get(IHyriPlayer.class);
            final IHyriParty requesterParty = this.partyManager.getPlayerParty(requester.getUniqueId());

            if (requesterParty == null) {
                return;
            }

            if (!this.partyManager.hasRequest(requesterParty.getId(), playerId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_NO_INVITATION_MESSAGE.asString(account).replace("%player%", requester.getNameWithRank()))));
                return;
            }

            this.partyManager.removeRequest(requesterParty.getId(), playerId);

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DENY_TARGET_MESSAGE.asString(account).replace("%player%", requester.getNameWithRank()))));

            for (UUID member : requesterParty.getMembers().keySet()) {
                PlayerUtil.sendComponent(member, createMessage(builder -> builder.append(BasicsMessage.PARTY_DENY_SENDER_MESSAGE.asString(IHyriPlayer.get(member)).replace("%player%", account.getNameWithRank()))));
            }
        });

        ctx.registerArgument("", output -> player.spigot().sendMessage(HELP.apply(player)));
        ctx.registerArgument("help", "/p help", output -> player.spigot().sendMessage(HELP.apply(player)));
        ctx.registerArgument("%player_online%",  "/p <player>", this.invitePlayer(player, account));
        ctx.registerArgument("invite %player_online%", "/p invite <player>", this.invitePlayer(player, account));
        ctx.registerArgument("add %player_online%", "/p add <player>", this.invitePlayer(player, account));

        super.handle(ctx);
    }

    private Consumer<CommandOutput> partyOutput(CommandContext ctx, IHyriParty party, Consumer<CommandOutput> action) {
        return output -> {
            final Player player = ctx.getSender();

            if (party != null) {
                action.accept(output);
            } else {
                ctx.setResult(new CommandResult(CommandResult.Type.SUCCESS));

                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DOESNT_HAVE_SENDER_MESSAGE.asString(player))));
            }
        };
    }

    private Consumer<CommandOutput> listParty(Player player, IHyriPlayer account, IHyriParty party) {
        return output -> {
            player.spigot().sendMessage(createMessage(builder -> {
                builder.append(BasicsMessage.PARTY_INFORMATION_MESSAGE.asString(account)
                        .replace("%creation_date%", TimeUtil.formatDate(party.getCreationDate()))
                        .replace("%members%", String.valueOf(party.getMembers().size()))
                        .replace("%total_members%", String.valueOf(PartyLimit.getMaxSlots(IHyriPlayer.get(party.getLeader())))))
                        .append("\n");

                final List<UUID> members = party.getMembers().keySet()
                        .stream()
                        .sorted((o1, o2) -> party.getRank(o2).getId() - party.getRank(o1).getId())
                        .collect(Collectors.toList()); // Sort members by their ranks

                for (UUID member : members) {
                    final HyriPartyRank rank = party.getRank(member);
                    final String rankDisplay = HyriLanguageMessage.get("party." + rank.getDisplayKey() + ".rank").getValue(account);
                    final IHyriPlayer memberAccount = IHyriPlayer.get(member);
                    final IHyriPlayerSession memberSession = IHyriPlayerSession.get(member);

                    builder.append(" ▪ ").color(ChatColor.DARK_GRAY)
                            .append("(" + rankDisplay.substring(0, 1).toUpperCase() + ")").color(ChatColor.GRAY).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GRAY + rankDisplay)))
                            .append(" ● ").color(memberSession == null ? ChatColor.RED : ChatColor.GREEN).event((HoverEvent) null)
                            .append(memberAccount.getNameWithRank());

                    if (memberSession != null) {
                        builder.append(" <> ").color(ChatColor.WHITE)
                                .append(memberSession.getServer()).color(ChatColor.AQUA);
                    }

                    if (members.indexOf(member) != members.size() - 1) { // Add new line but only if it's not the last player
                        builder.append("\n");
                    }
                }
            }));
        };
    }

    private Consumer<CommandOutput> invitePlayer(Player player, IHyriPlayer account) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer target = output.get(IHyriPlayer.class);
            final UUID targetId = target.getUniqueId();

            IHyriParty party = this.partyManager.getPlayerParty(playerId);
            if (party == null) {
                party = this.partyManager.createParty(playerId);

                final IHyriPlayerSession session = IHyriPlayerSession.get(playerId);

                session.setParty(party.getId());
                session.update();
            }

            if (playerId.equals(targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_YOURSELF_MESSAGE.asString(account))));
                return;
            }

            if (this.partyManager.hasRequest(party.getId(), targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_ALREADY_INVITED_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            if (party.hasMember(targetId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_ALREADY_IN_YOUR_MESSAGE.asString(account).replace("%player%", target.getNameWithRank()))));
                return;
            }

            final int partyLimit = PartyLimit.getMaxSlots(account);

            if (party.getMembers().size() >= partyLimit) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_LIMIT_MESSAGE.asString(account).replace("%limit%", String.valueOf(partyLimit)))));
                return;
            }

            final IHyriPlayerSession targetSession = IHyriPlayerSession.get(targetId);

            if (targetSession.getNickname().has() && !account.getRank().isStaff()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", targetSession.getNameWithRank()))));
                return;
            }

            final SettingsLevel level = target.getSettings().getPartyRequestsLevel();

            if ((level == SettingsLevel.NONE || (level == SettingsLevel.FRIENDS && !target.getFriends().has(playerId))) && !account.getRank().isStaff()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DOESNT_ACCEPT_MESSAGE.asString(account).replace("%player%", targetSession.getNameWithRank()))));
                return;
            }

            if (!party.getRank(playerId).canSendInvitations()) {
                this.dontHavePermission(player);
                return;
            }

            this.partyManager.sendRequest(party.getId(), playerId, targetId);

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_INVITATION_SENT_MESSAGE.asString(account).replace("%player%", targetSession.getNameWithRank()))));
        };
    }

    private Consumer<CommandOutput> joinParty(Player player, IHyriPlayer account) {
        return output -> {
            final UUID playerId = player.getUniqueId();
            final IHyriPlayer requester = output.get(IHyriPlayer.class);
            final IHyriParty party = this.partyManager.getPlayerParty(requester.getUniqueId());

            if (!this.partyManager.hasRequest(party.getId(), playerId) && party.isPrivate()) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_NO_INVITATION_MESSAGE.asString(account).replace("%player%", requester.getNameWithRank()))));
                return;
            }

            if (this.partyManager.getPlayerParty(playerId) != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_ALREADY_IN_ONE_MESSAGE.asString(account))));
                return;
            }

            if (party.getMembers().size() >= PartyLimit.getMaxSlots(IHyriPlayer.get(party.getLeader()))) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_LIMIT_TARGET_MESSAGE.asString(account))));
                return;
            }

            this.partyManager.removeRequest(party.getId(), playerId);

            party.addMember(playerId, HyriPartyRank.MEMBER);
        };
    }

    private boolean canEditRank(Player player, IHyriParty party, IHyriPlayer target) {
        final UUID targetId = target.getUniqueId();

        if (!this.isInParty(player, party, target)) {
            return false;
        }

        final HyriPartyRank playerRank = party.getRank(player.getUniqueId());

        if (!playerRank.canEditRank() || party.getRank(targetId).isSuperior(playerRank)) {
            this.dontHavePermission(player);
            return false;
        }
        return true;
    }

    private boolean isInParty(Player player, IHyriParty party, IHyriPlayer target) {
        if (!party.hasMember(target.getUniqueId())) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_NOT_IN_MESSAGE.asString(player).replace("%player%", target.getNameWithRank()))));
            return false;
        }
        return true;
    }

    private void dontHavePermission(Player player) {
        player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_NO_PERMISSION_MESSAGE.asString(player))));
    }

}
