package fr.hyriode.basics.friend;

import fr.hyriode.api.packet.HyriPacket;
import fr.hyriode.api.packet.IHyriPacketReceiver;
import fr.hyriode.api.player.model.HyriFriendRequest;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 23/04/2022 at 08:45
 */
public class FriendReceiver implements IHyriPacketReceiver {

    private final FriendModule friendModule;

    public FriendReceiver(FriendModule friendModule) {
        this.friendModule = friendModule;
    }

    @Override
    public void receive(String channel, HyriPacket packet) {
        if (packet instanceof HyriFriendRequest) {
            this.friendModule.onRequest((HyriFriendRequest) packet);
        }
    }

}
