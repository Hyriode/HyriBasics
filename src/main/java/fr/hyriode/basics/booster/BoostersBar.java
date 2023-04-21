package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.server.ILobbyAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.language.BasicsMessage;
import fr.hyriode.hyrame.bossbar.BossBar;
import fr.hyriode.hyrame.bossbar.BossBarAnimation;
import fr.hyriode.hyrame.bossbar.BossBarManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static fr.hyriode.hyrame.bossbar.BossBarAnimation.Evolution.Options;
import static fr.hyriode.hyrame.bossbar.BossBarAnimation.Evolution.Type;

/**
 * Created by AstFaster
 * on 20/04/2023 at 20:37
 */
public class BoostersBar {

    private Type currentType;
    private int boosterIndex;

    private final Player player;

    public BoostersBar(Player player) {
        this.player = player;
    }

    public void init() {
        this.currentType = Type.INCREASING;
        this.boosterIndex = 0;

        final BossBar bar = BossBarManager.setBar(this.player, "", 0.0f);

        this.nextBooster().accept(bar);
    }

    public void remove() {
        BossBarManager.removeBar(this.player);
    }

    private Consumer<BossBar> nextBooster() {
        return bar -> {
            final List<IHyriBooster> boosters = this.getBoosters();

            if (boosters.size() == 0) {
                this.remove();
                return;
            }

            if (this.boosterIndex >= boosters.size()) {
                this.boosterIndex = 0;
            }

            final IHyriBooster booster = boosters.get(this.boosterIndex);
            final BossBarAnimation.Evolution animation = new BossBarAnimation.Evolution(this.currentType, new Options(5 * 20L, this.nextBooster()));

            bar.setText(BasicsMessage.BOOSTER_BOSS_BAR.asString(this.player)
                    .replace("%boost%", String.valueOf(((int) (booster.getMultiplier() * 100 - 100))))
                    .replace("%game%", HyriAPI.get().getGameManager().getGameInfo(booster.getGame()).getDisplayName())
                    .replace("%owner%", IHyriPlayer.get(booster.getOwner()).getNameWithRank()));
            bar.applyAnimation(animation);

            this.currentType = this.currentType == Type.INCREASING ? Type.DECREASING : Type.INCREASING;
            this.boosterIndex++;
        };
    }

    private List<IHyriBooster> getBoosters() {
        final String serverType = HyriAPI.get().getServer().getType();

        if (serverType.equals(ILobbyAPI.TYPE)) {
            return HyriBasics.get().getBoosterModule().getBoosters();
        }

        return HyriBasics.get().getBoosterModule().getBoosters()
                .stream()
                .filter(booster -> booster.getGame().equals(serverType))
                .collect(Collectors.toList());
    }

}
