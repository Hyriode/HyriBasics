package fr.hyriode.basics.debug.gui.server.select;

import fr.hyriode.api.game.IHyriGameType;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 19:15
 */
public class ServerGameTypeGUI extends HyriInventory {

    public ServerGameTypeGUI(Player owner, List<IHyriGameType> types, Consumer<String> typeSelected) {
        super(owner, ChatColor.DARK_GRAY + "Séléctionner le type", dynamicSize(types.size()));

        types.sort(Comparator.comparingInt(IHyriGameType::getId));

        for (IHyriGameType gameType : types) {
            this.addItem(new ItemBuilder(Material.REDSTONE_COMPARATOR)
                            .withName(ChatColor.AQUA + gameType.getDisplayName())
                            .withLore(
                                    ChatColor.DARK_GRAY + "Nom réel: " + ChatColor.AQUA + gameType.getName(),
                                    ChatColor.DARK_GRAY + "Id: " + ChatColor.AQUA + gameType.getId(),
                                    "",
                                    ChatColor.DARK_AQUA + "Cliquer pour séléctionner")
                            .build(),
                    event -> typeSelected.accept(gameType.getName()));
        }
    }
}
