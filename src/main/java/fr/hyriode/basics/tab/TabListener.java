package fr.hyriode.basics.tab;

import fr.hyriode.basics.HyriBasics;
import fr.hyriode.hyrame.listener.HyriListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 12/11/2021 at 16:11
 */
public class TabListener extends HyriListener<HyriBasics> {

    public TabListener(HyriBasics plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getTabModule().onLogin(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getTabModule().onLogout(event.getPlayer());
    }

}
