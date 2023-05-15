package fr.hyriode.basics.tab;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 16/12/2021 at 18:15
 */
public class TabModule {

    private final Map<UUID, DefaultTab> tabs = new ConcurrentHashMap<>();

    public TabModule() {
        HyriAPI.get().getScheduler().schedule(() -> {
            for (DefaultTab tab : this.tabs.values()) {
                tab.update();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void onLogin(Player player) {
        this.tabs.put(player.getUniqueId(), new DefaultTab(player));
    }

    public void onLogout(Player player) {
        this.tabs.remove(player.getUniqueId());
    }

}
