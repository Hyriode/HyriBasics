package fr.hyriode.basics.debug.gui.server.select;

import fr.hyriode.api.world.IHyriWorld;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 18:38
 */
public class ServerMapGUI extends HyriInventory {

    public ServerMapGUI(Player owner, List<IHyriWorld> maps, Consumer<IHyriWorld> mapSelected) {
        super(owner, ChatColor.DARK_GRAY + "Séléctionner la carte", dynamicSize(maps.size()));

        for (IHyriWorld map : maps) {
            this.addItem(new ItemBuilder(Material.PAPER)
                    .withName(ChatColor.AQUA + map.getName())
                    .withLore("", ChatColor.DARK_AQUA + "Cliquer pour séléctionner")
                    .build(),
                    event -> mapSelected.accept(map));
        }
    }

}
