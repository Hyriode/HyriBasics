package fr.hyriode.basics.party;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.party.IHyriParty;
import fr.hyriode.api.party.event.*;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.language.BasicsMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static fr.hyriode.basics.party.PartyModule.createMessage;

/**
 * Created by AstFaster
 * on 29/04/2022 at 20:12
 */
public class PartyListener {

    @HyriEventHandler
    public void onJoin(HyriPartyJoinEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID newMember = event.getMember();
        final IHyriPlayer account = IHyriPlayer.get(newMember);

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            BaseComponent[] message;
            if (member.equals(newMember)) {
                message = createMessage(builder -> builder.append(BasicsMessage.PARTY_JOIN_PLAYER_MESSAGE.asString(account).replace("%player%", IHyriPlayer.get(party.getLeader()).getNameWithRank())));
            } else {
                message = createMessage(builder -> builder.append(BasicsMessage.PARTY_JOIN_OTHERS_MESSAGE.asString(player).replace("%player%", account.getNameWithRank())));
            }

            player.spigot().sendMessage(message);
        }
    }

    @HyriEventHandler
    public void onLeave(HyriPartyLeaveEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID oldMemberId = event.getMember();
        final Player oldMember = Bukkit.getPlayer(oldMemberId);
        final IHyriPlayer account = HyriAPI.get().getPlayerManager().getPlayer(oldMemberId);

        if (oldMember != null) {
            oldMember.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_LEFT_PLAYER_MESSAGE.asString(oldMember).replace("%player%", IHyriPlayer.get(party.getLeader()).getNameWithRank()))));
        }

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_LEFT_OTHERS_MESSAGE.asString(player).replace("%player%", account.getNameWithRank()))));
        }
    }

    @HyriEventHandler
    public void onKick(HyriPartyKickEvent event) {
        final UUID kicked = event.getMember();
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer kickedAccount = IHyriPlayer.get(kicked);
        final IHyriPlayer kicker = IHyriPlayer.get(event.getKicker());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_KICK_OTHERS_MESSAGE.asString(player)
                        .replace("%player%", kicker.getNameWithRank())
                        .replace("%kicked%", kickedAccount.getNameWithRank()))));
            }
        }

        final Player player = Bukkit.getPlayer(kicked);

        if (player != null) {
            player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_KICK_TARGET_MESSAGE.asString(player).replace("%player%", IHyriPlayer.get(party.getLeader()).getNameWithRank()))));
        }
    }

    @HyriEventHandler
    public void onLeader(HyriPartyLeaderEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer oldLeader = IHyriPlayer.get(event.getOldLeader());
        final IHyriPlayer newLeader = IHyriPlayer.get(event.getNewLeader());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_LEAD_TRANSFER_MESSAGE.asString(player)
                        .replace("%old_leader%", oldLeader.getNameWithRank())
                        .replace("%new_leader%", newLeader.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onDisband(HyriPartyDisbandEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final UUID leaderId = party.getLeader();
        final IHyriPlayer leader = IHyriPlayer.get(leaderId);

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player == null) {
                continue;
            }

            if (player.getUniqueId().equals(leaderId)) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DISBAND_PLAYER_MESSAGE.asString(player))));
            } else {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DISBAND_OTHERS_MESSAGE.asString(player).replace("%player%", leader.getNameWithRank()))));
            }
        }

        HyriAPI.get().getPartyManager().removeParty(party.getId());
    }

    @HyriEventHandler
    public void onPromote(HyriPartyPromoteEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer account = IHyriPlayer.get(event.getMember());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_PROMOTE_MESSAGE.asString(player).replace("%target%", account.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onDemote(HyriPartyDemoteEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final IHyriPlayer account = IHyriPlayer.get(event.getMember());

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append(BasicsMessage.PARTY_DEMOTE_MESSAGE.asString(player).replace("%player%", account.getNameWithRank()))));
            }
        }
    }

    @HyriEventHandler
    public void onChat(HyriPartyChatEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        final HyriPartyChatEvent.Action action = event.getAction();

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append((action == HyriPartyChatEvent.Action.ENABLED ? BasicsMessage.PARTY_CHAT_ENABLED_MESSAGE : BasicsMessage.PARTY_CHAT_DISABLED_MESSAGE).asString(player))));
            }
        }
    }

    @HyriEventHandler
    public void onAccess(HyriPartyAccessEvent event) {
        final IHyriParty party = event.getParty();

        if (party == null) {
            return;
        }

        for (UUID member : party.getMembers().keySet()) {
            final Player player = Bukkit.getPlayer(member);

            if (player != null) {
                player.spigot().sendMessage(createMessage(builder -> builder.append((party.isPrivate() ? BasicsMessage.PARTY_ACCESS_PRIVATE_MESSAGE : BasicsMessage.PARTY_ACCESS_PUBLIC_MESSAGE_MESSAGE).asString(player))));
            }
        }
    }

}
