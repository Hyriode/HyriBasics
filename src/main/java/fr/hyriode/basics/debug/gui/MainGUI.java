package fr.hyriode.basics.debug.gui;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.protocol.DebugProtocol;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Symbols;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 21:22
 */
public class MainGUI extends DebugGUI {

    public MainGUI(Player owner, HyriBasics plugin) {
        super(owner, "Menu principal", plugin);
        this.newUpdate(3 * 20L);

        this.addItems();
    }

    private void addItems() {
        final DebugProtocol protocol = this.plugin.getDebugProtocol();
        final HyggProxy lastProxy = protocol.getLastProxy();
        final HyggLimbo lastLimbo = protocol.getLastLimbo();
        final HyggServer lastServer = protocol.getLastServer();

        // Proxies info
        this.setItem(21, this.createInfoItem("Proxies", String.valueOf(HyriAPI.get().getProxyManager().getProxies().size()), lastProxy == null ? "?" : lastProxy.getName()));

        // Limbos info
        this.setItem(21, this.createInfoItem("Limbos", String.valueOf(HyriAPI.get().getLimboManager().getLimbos().size()), lastLimbo == null ? "?" : lastLimbo.getName()));

        // Servers info
        this.setItem(23, this.createInfoItem("Serveurs", String.valueOf(HyriAPI.get().getServerManager().getServers().size()), lastServer == null ? "?" : lastServer.getName()));
    }

    private ItemStack createInfoItem(String title, String total, String last) {
        return new ItemBuilder(Material.PAPER)
                .withName(ChatColor.DARK_AQUA + title + " " + ChatColor.DARK_GRAY + Symbols.LINE_VERTICAL_BOLD + ChatColor.GRAY + " Info")
                .withLore(
                        ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " Total: " + ChatColor.AQUA + total,
                        ChatColor.DARK_GRAY + Symbols.DOT_BOLD + ChatColor.GRAY + " Dernier: " + ChatColor.AQUA + last)
                .build();
    }

    @Override
    public void update() {
        this.addItems();
    }

}
