package fr.hyriode.basics.debug.protocol.receiver;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.packet.HyriPacket;
import fr.hyriode.api.packet.IHyriPacketReceiver;
import fr.hyriode.api.server.IHyriServer;
import fr.hyriode.basics.debug.protocol.packet.EditServerSlotsPacket;
import fr.hyriode.basics.debug.protocol.packet.EditServerStatePacket;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:18
 */
public class DevReceiver implements IHyriPacketReceiver {

    @Override
    public void receive(String channel, HyriPacket packet) {
        if (packet instanceof EditServerStatePacket) {
            final EditServerStatePacket statePacket = (EditServerStatePacket) packet;
            final String serverName = statePacket.getServer();
            final IHyriServer server = HyriAPI.get().getServer();

            if (server.getName().equals(serverName)) {
                server.setState(statePacket.getState());
            }
        } else if (packet instanceof EditServerSlotsPacket) {
            final EditServerSlotsPacket slotsPacket = (EditServerSlotsPacket) packet;
            final String serverName = slotsPacket.getServer();
            final IHyriServer server = HyriAPI.get().getServer();

            if (server.getName().equals(serverName)) {
                server.setSlots(slotsPacket.getSlots());
            }
        }
    }

}
