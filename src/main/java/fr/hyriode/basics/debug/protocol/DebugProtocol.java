package fr.hyriode.basics.debug.protocol;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.debug.protocol.packet.EditServerStatePacket;
import fr.hyriode.basics.debug.protocol.packet.EditServerSlotsPacket;
import fr.hyriode.basics.debug.protocol.receiver.DevReceiver;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.limbo.HyggLimboStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStartedEvent;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 08:18
 */
public class DebugProtocol {

    public static final String CHANNEL = "dev";

    private HyggServer lastServer;
    private HyggProxy lastProxy;
    private HyggLimbo lastLimbo;

    public DebugProtocol() {
        HyriAPI.get().getPubSub().subscribe(CHANNEL, new DevReceiver());

        final HyggEventBus eventBus = HyriAPI.get().getHyggdrasilManager().getHyggdrasilAPI().getEventBus();

        eventBus.subscribe(HyggServerStartedEvent.class, event -> this.lastServer = event.getServer());
        eventBus.subscribe(HyggProxyStartedEvent.class, event -> this.lastProxy = event.getProxy());
        eventBus.subscribe(HyggLimboStartedEvent.class, event -> this.lastLimbo = event.getLimbo());
    }

    public void editServerSlots(String server, int slots) {
        HyriAPI.get().getPubSub().send(CHANNEL, new EditServerSlotsPacket(server, slots));
    }

    public void editServerState(String server, HyggServer.State state) {
        HyriAPI.get().getPubSub().send(CHANNEL, new EditServerStatePacket(server, state));
    }

    public HyggServer getLastServer() {
        return this.lastServer;
    }

    public HyggProxy getLastProxy() {
        return this.lastProxy;
    }

    public HyggLimbo getLastLimbo() {
        return this.lastLimbo;
    }

}
