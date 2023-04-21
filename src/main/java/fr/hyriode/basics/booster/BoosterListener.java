package fr.hyriode.basics.booster;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.booster.BoosterEnabledEvent;
import fr.hyriode.api.booster.IHyriBooster;
import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.bossbar.BossBar;
import fr.hyriode.hyrame.game.HyriGame;
import fr.hyriode.hyrame.game.HyriGameState;
import fr.hyriode.hyrame.game.event.HyriGameStateChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 19/10/2022 at 14:01
 */
public class BoosterListener implements Listener {

    private final Map<UUID, BoostersBar> bars = new HashMap<>();

    private final BoosterModule boosterModule;

    public BoosterListener(BoosterModule boosterModule) {
        this.boosterModule = boosterModule;

        HyriAPI.get().getNetworkManager().getEventBus().register(this);
        HyriAPI.get().getEventBus().register(this);
        HyriBasics.get().getServer().getPluginManager().registerEvents(this, HyriBasics.get());
    }

    public void initBars() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.initBar(player);
        }
    }

    public void initBar(Player player) {
        final HyriGame<?> game = IHyrame.get().getGame();

        if (game == null || game.getState().isAccessible()) {
            final BoostersBar boostersBar = this.bars.getOrDefault(player.getUniqueId(), new BoostersBar(player));

            boostersBar.init();
        }
    }

    @HyriEventHandler
    public void onBooster(BoosterEnabledEvent event) {
        final IHyriBooster booster = event.getBooster();

        this.boosterModule.onBoosterEnabled(booster);
    }

    @HyriEventHandler
    public void onGameStart(HyriGameStateChangedEvent event) {
        if (event.getNewState() == HyriGameState.PLAYING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                final BoostersBar bar = this.bars.remove(player.getUniqueId());

                if (bar == null) {
                    continue;
                }

                bar.remove();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.initBar(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final BoostersBar boostersBar = this.bars.remove(player.getUniqueId());

        if (boostersBar != null) {
            boostersBar.remove();
        }
    }

}
