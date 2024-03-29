package fr.hyriode.basics.party;

import fr.hyriode.api.packet.HyriPacket;
import fr.hyriode.api.packet.IHyriPacketReceiver;
import fr.hyriode.api.party.HyriPartyRequest;

/**
 * Project: Hyrame
 * Created by AstFaster
 * on 29/04/2022 at 19:33
 */
public class PartyReceiver implements IHyriPacketReceiver {

    private final PartyModule partyModule;

    public PartyReceiver(PartyModule partyModule) {
        this.partyModule = partyModule;
    }

    @Override
    public void receive(String channel, HyriPacket packet) {
        if (packet instanceof HyriPartyRequest) {
            this.partyModule.onRequest((HyriPartyRequest) packet);
        }
    }

}
