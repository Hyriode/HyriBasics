package fr.hyriode.basics.util;

import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.Material;

/**
 * Created by AstFaster
 * on 16/12/2022 at 19:17
 */
public class GUIDesign {

    public static final HyriInventory.IDesign<Void> DOUBLE_LINES = (inventory, unused) -> {
        inventory.setHorizontalLine(0, 8, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
        inventory.setHorizontalLine(45, 53, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 9).withName(" ").build());
    };

}
