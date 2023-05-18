package fr.hyriode.basics.debug.gui.games;

import fr.hyriode.api.game.IHyriGameInfo;
import fr.hyriode.api.game.IHyriGameType;
import fr.hyriode.basics.HyriBasics;
import fr.hyriode.basics.debug.gui.DebugGUI;
import fr.hyriode.basics.util.BasicsHead;
import fr.hyriode.hyrame.anvilgui.AnvilGUI;
import fr.hyriode.hyrame.inventory.pagination.PaginatedItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.hyrame.utils.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by AstFaster
 * on 18/05/2023 at 10:32
 */
public class GameGUI extends DebugGUI {

    private final IHyriGameInfo gameInfo;

    public GameGUI(Player owner, IHyriGameInfo gameInfo) {
        super(owner, gameInfo.getName(), HyriBasics.get());
        this.gameInfo = gameInfo;

        this.setItem(49, ItemBuilder.asHead(BasicsHead.MONITOR_PLUS)
                        .withName(ChatColor.AQUA + "Ajouter un type")
                        .withLore(ChatColor.GRAY + "Ajoute un type disponible pour ce", ChatColor.GRAY + "jeu.", "", ChatColor.DARK_AQUA + "Cliquer pour ajouter")
                        .build(),
                event -> new AnvilGUI(this.plugin, this.owner, "Nom du type", null, false, player -> this.open(), null, null, (player, typeName) -> {
                    new AnvilGUI(this.plugin, this.owner, "Nom (affiché) du type", null, false, p -> this.open(), null, null, (p, typeDisplay) -> {
                        int higherId = -1;
                        for (IHyriGameType gameType : this.gameInfo.getTypes()) {
                            if (higherId < gameType.getId()) {
                                higherId = gameType.getId();
                            }
                        }

                        this.gameInfo.addType(higherId + 1, typeName, typeDisplay);
                        this.gameInfo.update();

                        this.owner.sendMessage(ChatColor.GREEN + "Type correctement ajouté.");
                        this.owner.playSound(this.owner.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

                        new GameGUI(this.owner, this.gameInfo).open();

                        return null;
                    }).open();

                    return null;
                }).open());

        this.setupItems();
    }

    private void setupItems() {
        final Pagination<PaginatedItem> pagination = this.paginationManager.getPagination();

        pagination.clear();

        for (IHyriGameType type : this.gameInfo.getTypes().stream().sorted(Comparator.comparingInt(IHyriGameType::getId)).collect(Collectors.toList())) {
            final ItemStack itemStack = new ItemBuilder(Material.MAP)
                    .withName(ChatColor.AQUA + type.getDisplayName())
                    .withLore(LORE_FORMATTER.apply("Nom", type.getName()), LORE_FORMATTER.apply("Id", String.valueOf(type.getId())), "", ChatColor.RED + "Clic-droit pour supprimer")
                    .withAllItemFlags()
                    .build();

            pagination.add(PaginatedItem.from(itemStack, event -> {
                if (event.isRightClick()) {
                    this.gameInfo.removeType(type.getName());
                    this.gameInfo.update();

                    this.owner.sendMessage(ChatColor.RED + "Le type a bien été supprimé.");
                    this.owner.playSound(this.owner.getLocation(), Sound.FIZZ, 0.5F, 1.0F);

                    this.setupItems();
                }
            }));
        }

        this.paginationManager.updateGUI();
    }

}
