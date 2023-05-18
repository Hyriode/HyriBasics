package fr.hyriode.basics.debug.gui.games;

import fr.hyriode.api.HyriAPI;
import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.game.IHyriGameManager;
import fr.hyriode.api.game.rotating.IHyriRotatingGameManager;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by AstFaster
 * on 18/05/2023 at 10:35
 */
public class RotatingGamesGUI extends DebugGUI {

    public RotatingGamesGUI(Player owner) {
        super(owner, "Jeux rotatifs", HyriBasics.get());

        this.setItem(49, ItemBuilder.asHead(BasicsHead.MONITOR_PLUS)
                .withName(ChatColor.AQUA + "Ajouter un jeu rotatif")
                .withLore(ChatColor.GRAY + "Ajoute un jeu rotatif à la liste", ChatColor.GRAY + "actuelle des jeux rotatifs présents", ChatColor.GRAY + "sur le serveur.", "", ChatColor.DARK_AQUA + "Cliquer pour ajouter")
                .build(),
                event -> new AnvilGUI(this.plugin, this.owner, "Nom du jeu à ajouter", null, false, player -> Bukkit.getScheduler().runTaskLater(IHyrame.get().getPlugin(), this::open, 1L), null, null, (player, gameName) -> {
                    final IHyriGameManager gameManager = HyriAPI.get().getGameManager();
                    final IHyriRotatingGameManager rotatingGameManager = gameManager.getRotatingGameManager();
                    final IHyriGameInfo gameInfo = gameManager.getGameInfo(gameName);

                    if (gameInfo == null) {
                        this.owner.sendMessage(ChatColor.RED + "Impossible de trouver un jeu appelé: " + gameName + ".");
                        return null;
                    }

                    rotatingGameManager.addRotatingGame(rotatingGameManager.getRotatingGames().size(), gameName);

                    new RotatingGamesGUI(this.owner).open();

                    return null;
                }).open());

        this.setItem(51, ItemBuilder.asHead(BasicsHead.RED_BUTTON)
                        .withName(ChatColor.AQUA + "Forcer la rotation")
                        .withLore(ChatColor.GRAY + "Force la rotation vers le prochain", ChatColor.GRAY + "jeu rotatif.", "", ChatColor.RED + "Clic-gauche pour forcer")
                        .build(),
                event -> {
                    HyriAPI.get().getGameManager().getRotatingGameManager().switchToNextRotatingGame();

                    this.owner.sendMessage(ChatColor.RED + "La rotation a bien été forcée.");
                    this.owner.playSound(this.owner.getLocation(), Sound.NOTE_PLING, 1.0F, 0.5F);

                    this.setupItems();
                });

        this.setupItems();
    }

    private void setupItems() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();
        final List<IHyriGameInfo> rotatingGames = HyriAPI.get().getGameManager().getRotatingGameManager().getRotatingGames();

        pagination.clear();

        for (int i = 0; i < rotatingGames.size(); i++) {
            final IHyriGameInfo game = rotatingGames.get(i);
            final ItemBuilder itemBuilder = new ItemBuilder(Material.PAPER)
                    .withName(ChatColor.AQUA + game.getDisplayName())
                    .withLore(LORE_FORMATTER.apply("Nom", game.getName()), LORE_FORMATTER.apply("Types", String.valueOf(game.getTypes().size())), "", LORE_FORMATTER.apply("Ordre", String.valueOf(i == 0 ? ChatColor.GREEN + "Actuel" : i)), "", ChatColor.RED + "Clic-droit pour enlever");

            if (i == 0) {
                itemBuilder.withGlow();
            }

            pagination.add(PaginatedItem.from(itemBuilder.build(), event -> {
                if (event.isRightClick()) {
                    HyriAPI.get().getGameManager().getRotatingGameManager().removeRotatingGame(game.getName());

                    this.setupItems();

                    this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);
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
