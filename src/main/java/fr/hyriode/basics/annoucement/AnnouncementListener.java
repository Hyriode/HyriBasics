package fr.hyriode.basics.annoucement;

import fr.hyriode.api.event.HyriEventHandler;
import fr.hyriode.basics.language.BasicsMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by AstFaster
 * on 08/12/2022 at 17:13
 */
public class AnnouncementListener {

    @HyriEventHandler
    public void onAnnouncement(AnnouncementEvent event) {
        final String message = event.getMessage();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(BasicsMessage.ANNOUNCEMENT_MESSAGE.asString(player).replace("%message%", message));
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2.0F, 0.5F);
        }
    }

}
