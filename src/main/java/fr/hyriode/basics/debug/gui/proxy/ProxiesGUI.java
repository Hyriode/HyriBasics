package fr.hyriode.basics.debug.gui.proxy;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.basics.debug.util.DevItemUtil;
import fr.hyriode.basics.debug.util.ProxyStateWrapper;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.basics.util.ConfirmGUI;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 20/05/2022 at 21:38
 */
public class ProxiesGUI extends DebugGUI {

    public ProxiesGUI(Player owner, HyriBasics plugin) {
        super(owner, "Proxies", plugin);
        this.usingPages = true;
        this.newUpdate(3 * 20L);

        this.paginationManager.setArea(new PaginationArea(9, 44));
        this.setupProxies();

        this.setItem(49, BasicsHead.MONITOR_PLUS.asItemBuilder()
                .withName(ChatColor.DARK_AQUA + "Démarrer un proxy")
                .withLore(
                        ChatColor.GRAY + "Démarre et ajoute un nouveau",
                        ChatColor.GRAY + "proxy à l'infrastructure",
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour démarrer")
                .build(),
                event -> {
                    new ConfirmGUI(this.owner).withConfirmCallback(e -> {
                        this.open();

                        this.owner.sendMessage(ChatColor.GREEN + "Démarrage en cours d'un nouveau proxy, merci de patienter...");

                        final long before = System.currentTimeMillis();

                        HyriAPI.get().getProxyManager().createProxy(proxy -> this.owner.sendMessage(ChatColor.GREEN + "'" + proxy.getName() + "' a démarré après " + (System.currentTimeMillis() - before) + "ms."));
                    }).withCancelCallback(e -> {
                        this.open();

                        this.owner.sendMessage(ChatColor.RED + "Action annulée.");
                    }).open();
                });
    }

    private void setupProxies() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        final Stream<HyggProxy> proxiesStream = new ArrayList<>(HyriAPI.get().getProxyManager().getProxies()).stream();
        final List<HyggProxy> proxies = proxiesStream.sorted(Comparator.comparingInt(o -> ProxyStateWrapper.from(o.getState()).getId())).collect(Collectors.toList());

        for (HyggProxy proxy : proxies) {
            final ItemStack itemStack = new ItemBuilder(DevItemUtil.createProxyItem(proxy))
                    .appendLore("", ChatColor.RED + "Clic droit pour arrêter")
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> {
                if (event.getClick().isRightClick()) {
                    if (HyriAPI.get().getProxyManager().getProxies().size() <= 1) {
                        this.owner.sendMessage(ChatColor.RED + "Il doit y avoir au minimum 1 proxy démarré!");
                        return;
                    }

                    new ConfirmGUI(this.owner).withConfirmCallback(e -> {
                        this.open();

                        final String proxyName = proxy.getName();

                        this.owner.sendMessage(ChatColor.GREEN + "Demande d'arrêt de '" + proxyName + "' envoyée.");

                        HyriAPI.get().getProxyManager().removeProxy(proxyName, () -> this.owner.sendMessage(ChatColor.GREEN + "'" + proxyName + "' a été arrêté."));
                    }).withCancelCallback(e -> {
                        this.open();

                        this.owner.sendMessage(ChatColor.RED + "Action annulée.");
                    }).open();
                }
            }));
        }

        this.paginationManager.updateGUI();
    }

    @Override
    public void update() {
        this.setupProxies();
    }

}
