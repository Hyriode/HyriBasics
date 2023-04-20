package fr.hyriode.basics.debug.protocol.packet;

import fr.hyriode.api.packet.HyriPacket;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:16
 */
public abstract class EditServerPacket extends HyriPacket {

    private final String server;

    public EditServerPacket(String server) {
        this.server = server;
    }

    public String getServer() {
        return this.server;
    }

}
