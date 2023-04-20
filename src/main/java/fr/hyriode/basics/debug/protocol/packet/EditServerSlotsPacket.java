package fr.hyriode.basics.debug.protocol.packet;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:17
 */
public class EditServerSlotsPacket extends EditServerPacket {

    private final int slots;

    public EditServerSlotsPacket(String server, int slots) {
        super(server);
        this.slots = slots;
    }

    public int getSlots() {
        return this.slots;
    }

}
