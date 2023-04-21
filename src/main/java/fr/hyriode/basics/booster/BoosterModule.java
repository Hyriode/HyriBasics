package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AstFaster
 * on 19/10/2022 at 14:02
 */
public class BoosterModule {

    private final List<IHyriBooster> boosters = new ArrayList<>();

    private final BoosterListener listener;

    public BoosterModule() {
        this.listener = new BoosterListener(this);

        HyriBasics.get().getServer().getScheduler().runTaskTimer(HyriBasics.get(), () -> {
            final int oldSize = this.boosters.size();

            this.boosters.clear();
            this.boosters.addAll(HyriAPI.get().getBoosterManager().getActiveBoosters());

            if (oldSize == 0 && this.boosters.size() != 0) {
                this.listener.initBars();
            }
        }, 0, 10 * 20L);
    }

    public void onBoosterEnabled(IHyriBooster booster) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final String message = BasicsMessage.BOOSTER_MESSAGE.asString(player).replace("%game%", HyriAPI.get().getGameManager().getGameInfo(booster.getGame()).getDisplayName())
                    .replace("%player%", IHyriPlayer.get(booster.getOwner()).getNameWithRank())
                    .replace("%multiplier%", String.valueOf((int) (booster.getMultiplier() * 100 - 100)));

            player.sendMessage(message);
        }
    }

    public List<IHyriBooster> getBoosters() {
        return this.boosters;
    }

}
