package fr.hyriode.basics.afk;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.limbo.IHyriLimboManager;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

import java.util.UUID;

/**
 * Created by AstFaster
 * on 04/02/2023 at 22:33
 */
public class AFKPlayer {

    private final UUID playerId;

    private long lastMovement;
    private int elapsedMinutes;

    public AFKPlayer(UUID playerId) {
        this.playerId = playerId;
    }

    void onMove() {
        this.lastMovement = System.currentTimeMillis();
        this.elapsedMinutes = 0;
    }

    void onMinute() {
        this.elapsedMinutes++;

        if (this.elapsedMinutes == AFKModule.MAX_THRESHOLD) { // Player was afk for too long
            final IHyriLimboManager limboManager = HyriAPI.get().getLimboManager();
            final HyggLimbo limbo = limboManager.getBestLimbo(HyggLimbo.Type.AFK);

            if (limbo != null) {
                limboManager.sendPlayerToLimbo(this.playerId, limbo.getName());
            }
        }
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public long getLastMovement() {
        return this.lastMovement;
    }

}
