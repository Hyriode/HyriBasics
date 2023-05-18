package fr.hyriode.basics.debug.gui.games;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.inventory.pagination.PaginationArea;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 18/05/2023 at 10:29
 */
public class GamesGUI extends DebugGUI {

    public GamesGUI(Player owner,HyriBasics plugin) {
        super(owner, "Jeux", plugin);
        this.usingPages = true;
        this.paginationManager.setArea(new PaginationArea(20, 33));

        this.newUpdate(5 * 20L);

        this.setupItems();
    }

    private void setupItems() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (IHyriGameInfo gameInfo : HyriAPI.get().getGameManager().getGamesInfo()) {
            final ItemStack itemStack = new ItemBuilder(Material.PAPER)
                    .withName(ChatColor.AQUA + gameInfo.getDisplayName())
                    .withLore(LORE_FORMATTER.apply("Nom", gameInfo.getName()), LORE_FORMATTER.apply("Types", String.valueOf(gameInfo.getTypes().size())), "", ChatColor.DARK_AQUA + "Clic-gauche pour voir", ChatColor.RED + "Clic-droit pour supprimer")
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> {
                if (event.isRightClick()) {
                    HyriAPI.get().getGameManager().deleteGameInfo(gameInfo.getName());

                    this.setupItems();

                    this.owner.sendMessage(ChatColor.RED + "Le jeu a bien été supprimé.");
                    this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
                } else if (event.isLeftClick()) {
                    new GameGUI(this.owner, gameInfo).open();
                }
            }));
        }

        this.paginationManager.updateGUI();
    }

    @Override
    public void update() {
        this.setupItems();
    }

}
