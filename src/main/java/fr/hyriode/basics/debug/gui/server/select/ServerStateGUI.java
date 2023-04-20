package fr.hyriode.basics.debug.gui.server.select;

import fr.hyriode.basics.debug.util.ServerStateWrapper;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyrame.inventory.HyriInventory;
import fr.hyriode.hyrame.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Project: Hyriode
 * Created by AstFaster
 * on 22/05/2022 at 15:51
 */
public class ServerStateGUI extends HyriInventory {

    public ServerStateGUI(Player owner, Consumer<HyggServer.State> stateSelected) {
        super(owner, ChatColor.DARK_GRAY + "Changer l'état du serveur", 9);

        for (ServerStateWrapper state : ServerStateWrapper.values()) {
            final ItemStack itemStack = new ItemBuilder(state.getItem())
                    .withName(state.getDisplayName())
                    .withLore(state.getLore())
                    .appendLore("", ChatColor.DARK_AQUA + "Cliquer pour séléctionner")
                    .build();

            this.addItem(itemStack, event -> stateSelected.accept(state.getInitial()));
        }
    }

}
