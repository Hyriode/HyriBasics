package fr.hyriode.basics.party;

import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.rank.type.HyriPlayerRankType;

import java.util.function.Predicate;

/**
 * Created by AstFaster
 * on 23/04/2022 at 12:51
 */
public enum PartyLimit {

    PLAYER(HyriPlayerRankType.PLAYER, 5),
    VIP(HyriPlayerRankType.VIP, 10),
    VIP_PLUS(HyriPlayerRankType.VIP_PLUS, 15),
    EPIC(HyriPlayerRankType.EPIC, 20),
    EPIC_PLUS(IHyriPlayer::hasHyriPlus, 30),
    PARTNER(HyriPlayerRankType.PARTNER, 50);

    private final Predicate<IHyriPlayer> validation;
    private final int maxSlots;

    PartyLimit(Predicate<IHyriPlayer> validation, int maxSlots) {
        this.validation = validation;
        this.maxSlots = maxSlots;
    }

    PartyLimit(HyriPlayerRankType rankType, int maxSlots) {
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
