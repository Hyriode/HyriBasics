package fr.hyriode.basics.afk;

import fr.hyriode.basics.HyriBasics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AstFaster
 * on 04/02/2023 at 19:49
 */
public class AFKModule implements Listener {

    public static final int MAX_THRESHOLD = 10; // (in minutes)

    private final Map<UUID, AFKPlayer> players = new HashMap<>();

    public AFKModule() {
        HyriBasics.get().getServer().getPluginManager().registerEvents(this, HyriBasics.get());
        HyriBasics.get().getServer().getScheduler().runTaskTimer(HyriBasics.get(), () -> {
            for (AFKPlayer player : this.players.values()) {
                player.onMinute();
            }
        }, 0, 20 * 60);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final UUID playerId = event.getPlayer().getUniqueId();

        this.players.put(playerId, new AFKPlayer(playerId));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.players.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final AFKPlayer afkPlayer = this.players.get(event.getPlayer().getUniqueId());

        if (afkPlayer != null) {
            afkPlayer.onMove();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final AFKPlayer afkPlayer = this.players.get(event.getWhoClicked().getUniqueId());

        if (afkPlayer != null) {
            afkPlayer.onMove();
        }
    }

}
