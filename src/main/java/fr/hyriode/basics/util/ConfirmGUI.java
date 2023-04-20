package fr.hyriode.basics.util;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 20:40
 */
public class ConfirmGUI extends HyriInventory {

    private Consumer<InventoryClickEvent> confirm;
    private Consumer<InventoryClickEvent> cancel;

    public ConfirmGUI(Player owner) {
        super(owner, "Confirmer", 3 * 9);

        this.setItem(12, new ItemBuilder(Material.STAINED_GLASS, 1, 5)
                .withName(ChatColor.GREEN + "Confirmer")
                .withLore(ChatColor.GRAY + "Confirmer l'action")
                .build(),
                event -> {
                    this.owner.closeInventory();
                    this.confirm.accept(event);
                });

        this.setItem(14, new ItemBuilder(Material.STAINED_GLASS, 1, 14)
                .withName(ChatColor.RED + "Annuler")
                .withLore(ChatColor.GRAY + "Annuler l'action")
                .build(),
                event -> {
                    this.owner.closeInventory();
                    this.cancel.accept(event);
                });
    }

    public ConfirmGUI withConfirmCallback(Consumer<InventoryClickEvent> confirm) {
        this.confirm = confirm;
        return this;
    }

    public ConfirmGUI withCancelCallback(Consumer<InventoryClickEvent> cancel) {
        this.cancel = cancel;
        return this;
    }

}
