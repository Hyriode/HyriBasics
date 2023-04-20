package fr.hyriode.basics.debug.gui.server.select;

import fr.hyriode.api.game.IHyriGameInfo;
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
 * on 22/05/2022 at 19:15
 */
public class ServerTypeGUI extends HyriInventory {

    public ServerTypeGUI(Player owner, List<IHyriGameInfo> gamesInfo, Consumer<IHyriGameInfo> typeSelected, Runnable lobbySelected) {
        super(owner, ChatColor.DARK_GRAY + "Séléctionner le type", dynamicSize(gamesInfo.size()));

        this.setItem(0, new ItemBuilder(Material.WATCH)
                .withGlow()
                .withName(ChatColor.AQUA + "Lobby")
                .withLore(
                        ChatColor.DARK_GRAY + "Nom réel: " + ChatColor.AQUA + "lobby",
                        "",
                        ChatColor.DARK_AQUA + "Cliquer pour séléctionner")
                .build(),
                event -> lobbySelected.run());

        for (IHyriGameInfo gameInfo : gamesInfo) {
            this.addItem(new ItemBuilder(Material.COMPASS)
                            .withName(ChatColor.AQUA + gameInfo.getDisplayName())
                            .withLore(
                                    ChatColor.DARK_GRAY + "Nom réel: " + ChatColor.AQUA + gameInfo.getName(),
                                    ChatColor.DARK_GRAY + "Types de jeu: " + ChatColor.AQUA + gameInfo.getTypes().size(),
                                    "",
                                    ChatColor.DARK_AQUA + "Cliquer pour séléctionner")
                            .build(),
                    event -> typeSelected.accept(gameInfo));
        }
    }
}
