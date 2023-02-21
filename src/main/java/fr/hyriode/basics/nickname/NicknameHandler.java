package fr.hyriode.basics.nickname;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.player.IHyriPlayerSession;
import fr.hyriode.api.player.model.IHyriNickname;
import fr.hyriode.hyrame.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by AstFaster
 * on 21/04/2022 at 21:20
 */
public class NicknameHandler implements Listener {

    private final NicknameModule nicknameModule;

    public NicknameHandler(NicknameModule nicknameModule) {
        this.nicknameModule = nicknameModule;

        HyriAPI.get().getEventBus().register(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final IHyriPlayerSession session = IHyriPlayerSession.get(player.getUniqueId());

        if (session == null) {
            return;
        }

        final IHyriNickname nickname = session.getNickname();

        if (nickname.has()) {
            ThreadUtil.ASYNC_EXECUTOR.execute(() -> this.nicknameModule.applyNickname(player, nickname.getName(), nickname.getSkin()));
        }
    }

}
