package fr.hyriode.basics.debug.gui.server;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.basics.debug.util.DevItemUtil;
import fr.hyriode.basics.debug.util.ServerStateWrapper;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
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
public class ServersGUI extends DebugGUI {

    public ServersGUI(Player owner, HyriBasics plugin) {
        super(owner, "Serveurs", plugin);
        this.usingPages = true;
        this.newUpdate(3 * 20L);

        this.paginationManager.setArea(new PaginationArea(9, 44));
        this.setupServers();

        this.setItem(49, BasicsHead.MONITOR_PLUS.asItemBuilder()
                        .withName(ChatColor.DARK_AQUA + "Démarrer un serveur")
                        .withLore(
                                ChatColor.GRAY + "Démarre et ajoute un nouveau",
                                ChatColor.GRAY + "serveur à l'infrastructure",
                                "",
                                ChatColor.DARK_AQUA + "Cliquer pour démarrer")
                        .build(),
                event -> new ServerCreateGUI(this.owner, this.plugin).open());
    }

    private void setupServers() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        final Stream<HyggServer> serversStream = new ArrayList<>(HyriAPI.get().getServerManager().getServers()).stream();
        final List<HyggServer> servers = serversStream.sorted(Comparator.comparingInt(o -> ServerStateWrapper.from(o.getState()).getId())).collect(Collectors.toList());

        for (HyggServer server : servers) {
            final ItemStack itemStack = new ItemBuilder(DevItemUtil.createServerItem(server))
                    .appendLore("", ChatColor.DARK_AQUA + "Cliquer pour intéragir")
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> new ServerGUI(this.plugin, this.owner, this.name, server.getName()).open()));
        }

        this.paginationManager.updateGUI();
    }

    @Override
    public void update() {
       this.setupServers();
    }

}
