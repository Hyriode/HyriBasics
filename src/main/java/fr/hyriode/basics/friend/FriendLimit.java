package fr.hyriode.basics.friend;

import fr.hyriode.api.rank.PlayerRank;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 12:51
 */
public enum FriendLimit {

    PLAYER(PlayerRank.PLAYER, 10),
    VIP(PlayerRank.VIP, 20),
    VIP_PLUS(PlayerRank.VIP_PLUS, 35),
    EPIC(PlayerRank.EPIC, 50),
    PARTNER(PlayerRank.PARTNER, 75);

    private final PlayerRank associatedRank;
    private final int maxFriends;

    FriendLimit(PlayerRank associatedRank, int maxFriends) {
        this.associatedRank = associatedRank;
        this.maxFriends = maxFriends;
    }

    public static int getMaxFriends(PlayerRank rankType) {
        for (FriendLimit limit : values()) {
            if (limit.getAssociatedRank() == rankType) {
                return limit.getMaxFriends();
            }
        }
        return -1;
    }

    public PlayerRank getAssociatedRank() {
        return this.associatedRank;
    }

    public int getMaxFriends() {
        return this.maxFriends;
    }
}
