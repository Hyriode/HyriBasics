package fr.hyriode.basics.leveling;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.api.leveling.event.NetworkLevelEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 30/04/2022 at 23:29
 */
public class LevelingListener {

    private final LevelingModule levelingModule;

    public LevelingListener(LevelingModule levelingModule) {
        this.levelingModule = levelingModule;
    }

    @HyriEventHandler
    public void onLevelUp(NetworkLevelEvent event) {
        if (!event.getLeveling().equals("network")) {
            return;
        }

        final Player player = Bukkit.getPlayer(event.getPlayerId());

        if (player != null) {
            this.levelingModule.onLevelUp(player, event.getOldLevel(), event.getNewLevel());
        }
    }

}
