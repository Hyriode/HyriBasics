package fr.hyriode.basics.debug.gui.limbo;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.basics.debug.util.DevItemUtil;
import fr.hyriode.basics.debug.util.LimboStateWrapper;
import fr.hyriode.basics.util.ConfirmGUI;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
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
public class LimbosGUI extends DebugGUI {

    public LimbosGUI(Player owner, HyriBasics plugin) {
        super(owner, "Limbos", plugin);
        this.usingPages = true;
        this.newUpdate(3 * 20L);

        this.paginationManager.setArea(new PaginationArea(9, 45));

        this.setupProxies();
    }

    private void setupProxies() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        final Stream<HyggLimbo> limbosStream = new ArrayList<>(HyriAPI.get().getLimboManager().getLimbos()).stream();
        final List<HyggLimbo> limbos = limbosStream.sorted(Comparator.comparingInt(o -> LimboStateWrapper.from(o.getState()).getId())).collect(Collectors.toList());

        for (HyggLimbo limbo : limbos) {
            final ItemStack itemStack = new ItemBuilder(DevItemUtil.createLimboItem(limbo))
                    .appendLore("", ChatColor.RED + "Clic droit pour arrêter")
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> {
                if (event.getClick().isRightClick()) {
                    new ConfirmGUI(this.owner).withConfirmCallback(e -> {
                        this.open();

                        final String limboName = limbo.getName();

                        this.owner.sendMessage(ChatColor.GREEN + "Demande d'arrêt de '" + limboName + "' envoyée.");

                        HyriAPI.get().getLimboManager().removeLimbo(limboName, () -> this.owner.sendMessage(ChatColor.GREEN + "'" + limboName + "' a été arrêté."));
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
