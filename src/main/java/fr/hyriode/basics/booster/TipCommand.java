package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.booster.IHyriBoosterManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.CommandContext;
import fr.hyriode.hyrame.command.CommandInfo;
import fr.hyriode.hyrame.command.CommandUsage;
import fr.hyriode.hyrame.command.HyriCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 10/12/2022 at 19:24
 */
public class TipCommand extends HyriCommand<HyriBasics> {

    public TipCommand(HyriBasics plugin) {
        super(plugin, new CommandInfo("tip")
                .withAliases("merci", "thank", "thanks")
                .withUsage(new CommandUsage().withStringMessage(player -> "/tip"))
                .withDescription("Thank all the players that applied a booster on the network."));
    }

    @Override
    public void handle(CommandContext ctx) {
        final Player player = ctx.getSender();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final IHyriBoosterManager boosterManager = HyriAPI.get().getBoosterManager();
        final List<IHyriBooster> boosters = new ArrayList<>();

        for (IHyriBooster booster : boosterManager.getActiveBoosters()) {
            if (boosterManager.hasThanked(booster.getIdentifier(), player.getUniqueId()) || booster.getOwner().equals(player.getUniqueId())) {
                continue; // Check if the player already thanked or if the booster is his own
            }

            boosters.add(booster);
        }

        if (boosters.size() == 0) {
            player.sendMessage(BasicsMessage.COMMAND_TIP_NO_BOOSTER.asString(account));
            return;
        }

        int totalHyris = 0;

        final StringBuilder formattedBoosters = new StringBuilder();

        for (IHyriBooster booster : boosters) {
            final UUID owner = booster.getOwner();
            final IHyriPlayer ownerAccount = IHyriPlayer.get(owner);
            final long playerHyris = this.calculatePlayerHyris(booster.getMultiplier());
            final long ownerHyris = this.calculateOwnerHyris(booster.getMultiplier());

            ownerAccount.getHyris().add(ownerHyris).withMultiplier(false).exec(); // Add Hyris to the owner of the booster (to thank him)
            ownerAccount.update();
            account.getHyris().add(playerHyris).withMultiplier(false).exec(); // Add Hyris to the player that thanks
            account.update();

            totalHyris += playerHyris;

            if (HyriAPI.get().getPlayerManager().isOnline(owner)) {
                HyriAPI.get().getPlayerManager().sendMessage(owner, BasicsMessage.COMMAND_TIP_BOOSTER_OWNER.asString(ownerAccount)
                        .replace("%hyris%", String.valueOf(ownerHyris))
                        .replace("%player%", account.getNameWithRank()));
            }

            boosterManager.addThank(booster.getIdentifier(), player.getUniqueId()); // Save the thank

            formattedBoosters.append(ChatColor.DARK_GRAY + " ▪ ")
                    .append(ownerAccount.getNameWithRank())
                    .append(ChatColor.WHITE).append(" <> ")
                    .append(ChatColor.AQUA).append(HyriAPI.get().getGameManager().getGameInfo(booster.getGame()).getDisplayName())
                    .append(ChatColor.GRAY).append(" (+")
                    .append((int) booster.getMultiplier() * 100)
                    .append("%)\n");
        }

        player.sendMessage(BasicsMessage.COMMAND_TIP_PLAYER.asString(account)
                .replace("%boosters%", formattedBoosters.toString())
                .replace("%hyris%", String.valueOf(totalHyris)));
    }

    private long calculateOwnerHyris(double multiplier) {
        if (multiplier >= 3.0D) {
            return 75L;
        } else if (multiplier >= 2.5D) {
            return 50L;
        } else if (multiplier >= 2.0D) {
          return 35L;
        } else {
            return 25L;
        }
    }

    private long calculatePlayerHyris(double multiplier) {
        if (multiplier >= 3.0D) {
            return 200L;
        } else if (multiplier >= 2.5D) {
            return 145L;
        } else if (multiplier >= 2.0D) {
            return 85L;
        } else {
            return 50L;
        }
    }

}
