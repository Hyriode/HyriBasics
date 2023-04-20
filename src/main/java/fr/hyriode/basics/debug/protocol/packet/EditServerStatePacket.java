package fr.hyriode.basics.debug.protocol.packet;

import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:16
 */
public class EditServerStatePacket extends EditServerPacket {

    private final HyggServer.State state;

    public EditServerStatePacket(String server, HyggServer.State state) {
        super(server);
        this.state = state;
    }

    public HyggServer.State getState() {
        return this.state;
    }

}
