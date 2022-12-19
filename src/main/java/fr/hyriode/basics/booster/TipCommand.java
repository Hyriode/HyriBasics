package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.booster.IHyriBoosterManager;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.command.HyriCommand;
import fr.hyriode.hyrame.command.HyriCommandContext;
import fr.hyriode.hyrame.command.HyriCommandInfo;
import fr.hyriode.hyrame.command.HyriCommandType;
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
        super(plugin, new HyriCommandInfo("tip")
                .withAliases("merci", "thank", "thanks")
                .withType(HyriCommandType.PLAYER)
                .withUsage("/tip")
                .withDescription("Thank all the players that applied a booster on the network."));
    }

    @Override
    public void handle(HyriCommandContext ctx) {
        final Player player = (Player) ctx.getSender();
        final IHyriPlayer account = IHyriPlayer.get(player.getUniqueId());
        final IHyriBoosterManager boosterManager = HyriAPI.get().getBoosterManager();
        final List<IHyriBooster> boosters = new ArrayList<>();

        for (IHyriBooster booster : boosterManager.getBoosters()) {
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

            ownerAccount.getHyris().add(25).withMultiplier(false).exec(); // Add 25 Hyris to the owner of the booster (to thank him)
            ownerAccount.update();
            account.getHyris().add(50).withMultiplier(false).exec(); // Add 50 Hyris to the player that thanks
            account.update();

            totalHyris += 50;

            if (HyriAPI.get().getPlayerManager().isOnline(owner)) {
                HyriAPI.get().getPlayerManager().sendMessage(owner, BasicsMessage.COMMAND_TIP_BOOSTER_OWNER.asString(ownerAccount)
                        .replace("%hyris%", String.valueOf(25))
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

}
