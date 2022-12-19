package fr.hyriode.basics.leveling;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 30/04/2022 at 23:29
 */
public class LevelingModule {

    public LevelingModule() {
        HyriAPI.get().getNetworkManager().getEventBus().register(new LevelingListener(this));
    }

    public void onLevelUp(Player player, int oldLevel, int newLevel) {
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1F);
        player.sendMessage(BasicsMessage.LEVELING_MESSAGE.asString(player)
                .replace("%old_level%", String.valueOf(oldLevel))
                .replace("%new_level%", String.valueOf(newLevel)));
    }

}
