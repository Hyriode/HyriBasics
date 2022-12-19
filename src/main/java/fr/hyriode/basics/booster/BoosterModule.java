package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 19/10/2022 at 14:02
 */
public class BoosterModule {

    public BoosterModule() {
        HyriAPI.get().getNetworkManager().getEventBus().register(new BoosterListener(this));
    }

    public void onBoosterEnabled(IHyriBooster booster) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final String message = BasicsMessage.BOOSTER_MESSAGE.asString(player).replace("%game%", HyriAPI.get().getGameManager().getGameInfo(booster.getGame()).getDisplayName())
                    .replace("%player%", IHyriPlayer.get(booster.getOwner()).getNameWithRank())
                    .replace("%multiplier%", String.valueOf((int) booster.getMultiplier() * 100));

            player.sendMessage(message);
        }
    }

}
