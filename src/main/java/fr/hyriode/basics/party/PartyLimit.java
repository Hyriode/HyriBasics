package fr.hyriode.basics.party;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.PlayerRank;

import java.util.function.Predicate;

/**
 * Created by AstFaster
 * on 23/04/2022 at 12:51
 */
public enum PartyLimit {

    PLAYER(PlayerRank.PLAYER, 5),
    VIP(PlayerRank.VIP, 10),
    VIP_PLUS(PlayerRank.VIP_PLUS, 15),
    EPIC(PlayerRank.EPIC, 20),
    EPIC_PLUS(account -> account.getHyriPlus().has(), 30),
    PARTNER(PlayerRank.PARTNER, 50);

    private final Predicate<IHyriPlayer> validation;
    private final int maxSlots;

    PartyLimit(Predicate<IHyriPlayer> validation, int maxSlots) {
        this.validation = validation;
        this.maxSlots = maxSlots;
    }

    PartyLimit(PlayerRank rankType, int maxSlots) {
        this(account -> account.getRank().is(rankType), maxSlots);
    }

    public static int getMaxSlots(IHyriPlayer account) {
        if (account.getRank().isStaff()) {
            return PARTNER.getMaxSlots();
        }
        for (PartyLimit limit : values()) {
            if (limit.getValidation().test(account)) {
                return limit.getMaxSlots();
            }
        }
        return -1;
    }

    public Predicate<IHyriPlayer> getValidation() {
        return this.validation;
    }

    public int getMaxSlots() {
        return this.maxSlots;
    }

}
